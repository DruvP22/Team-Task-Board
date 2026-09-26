const API_BASE_URL = "http://localhost:8080/api";

function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}

async function request<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = getToken();

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({ message: response.statusText }));
    throw new Error(errorBody.message || "Request failed");
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export const api = {
  login: (email: string, password: string) =>
    request<{ token: string }>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),

  register: (email: string, password: string, role: string) =>
    request<{ token: string }>("/auth/register", {
      method: "POST",
      body: JSON.stringify({ email, password, role }),
    }),

  getTasks: (page = 0, size = 20) =>
    request<{ content: Task[]; totalElements: number }>(
      `/tasks?page=${page}&size=${size}`
    ),

  createTask: (task: {
    title: string;
    description?: string;
    priority: string;
    projectId: number;
    assigneeId?: number;
  }) =>
    request<Task>("/tasks", {
      method: "POST",
      body: JSON.stringify(task),
    }),

  changeTaskStatus: (id: number, status: string) =>
    request<Task>(`/tasks/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    }),

  getProjects: () => request<Project[]>("/projects"),

    getTaskById: (id: number) => request<Task>(`/tasks/${id}`),
};

export interface Task {
  id: number;
  title: string;
  description: string;
  status: "BACKLOG" | "IN_PROGRESS" | "REVIEW" | "DONE";
  priority: "LOW" | "MEDIUM" | "HIGH";
  assigneeEmail: string | null;
  projectId: number;
  createdAt: string;
  updatedAt: string;
}

export interface Project {
  id: number;
  name: string;
  description: string;
}