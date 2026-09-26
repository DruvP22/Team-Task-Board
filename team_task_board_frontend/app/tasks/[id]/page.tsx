"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { api, Task } from "@/lib/api";

const NEXT_STATUS: Record<Task["status"], Task["status"] | null> = {
  BACKLOG: "IN_PROGRESS",
  IN_PROGRESS: "REVIEW",
  REVIEW: "DONE",
  DONE: null,
};

const PREV_STATUS: Record<Task["status"], Task["status"] | null> = {
  BACKLOG: null,
  IN_PROGRESS: "BACKLOG",
  REVIEW: "IN_PROGRESS",
  DONE: "REVIEW",
};

const STATUS_LABEL: Record<Task["status"], string> = {
  BACKLOG: "Backlog",
  IN_PROGRESS: "In Progress",
  REVIEW: "Review",
  DONE: "Done",
};

const STATUS_STYLE: Record<Task["status"], string> = {
  BACKLOG: "bg-slate-100 text-slate-600",
  IN_PROGRESS: "bg-sky-100 text-sky-700",
  REVIEW: "bg-violet-100 text-violet-700",
  DONE: "bg-emerald-100 text-emerald-700",
};

const PRIORITY_STYLE: Record<Task["priority"], string> = {
  LOW: "bg-slate-100 text-slate-600",
  MEDIUM: "bg-amber-100 text-amber-700",
  HIGH: "bg-rose-100 text-rose-700",
};

export default function TaskDetailPage() {
  const params = useParams();
  const router = useRouter();
  const id = Number(params.id);

  const [task, setTask] = useState<Task | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [moving, setMoving] = useState(false);

  useEffect(() => {
    loadTask();
  }, [id]);

  async function loadTask() {
    setLoading(true);
    try {
      const data = await api.getTaskById(id);
      setTask(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load task");
    } finally {
      setLoading(false);
    }
  }

  async function moveStatus(newStatus: Task["status"]) {
  if (!task) return;
  setMoving(true);
  try {
    await api.changeTaskStatus(task.id, newStatus);
    await loadTask();
  } catch (err) {
    alert(err instanceof Error ? err.message : "Failed to update status");
  } finally {
    setMoving(false);
  }
}

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-slate-400">
        Loading task...
      </div>
    );
  }

  if (error || !task) {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-rose-500">
        {error || "Task not found."}
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="mx-auto max-w-2xl px-6 py-10">
        <button
          onClick={() => router.push("/board")}
          className="text-sm text-slate-400 hover:text-slate-600 mb-6"
        >
          ← Back to board
        </button>

        <div className="rounded-lg bg-white border border-slate-200 p-6">
          <div className="flex gap-2 mb-4">
            <span className={`text-xs px-2 py-1 rounded ${STATUS_STYLE[task.status]}`}>
              {STATUS_LABEL[task.status]}
            </span>
            <span className={`text-xs px-2 py-1 rounded ${PRIORITY_STYLE[task.priority]}`}>
              {task.priority}
            </span>
          </div>

          <h1 className="text-lg font-semibold text-slate-900 mb-2">{task.title}</h1>

          {task.description && (
            <p className="text-sm text-slate-500 mb-6">{task.description}</p>
          )}

          <div className="text-xs text-slate-400 space-y-1 border-t border-slate-100 pt-4">
            <p>Project ID: {task.projectId}</p>
            <p>Assignee: {task.assigneeEmail ?? "Unassigned"}</p>
            <p>Created: {new Date(task.createdAt).toLocaleString()}</p>
            <p>Updated: {new Date(task.updatedAt).toLocaleString()}</p>
          </div>

                    <div className="mt-6 flex gap-2">
            {PREV_STATUS[task.status] && (
              <button
                onClick={() => moveStatus(PREV_STATUS[task.status]!)}
                disabled={moving}
                className="flex-1 rounded-md border border-slate-200 text-slate-600 text-sm font-medium py-2 hover:bg-slate-50 disabled:opacity-50"
              >
                ← Move to {STATUS_LABEL[PREV_STATUS[task.status]!]}
              </button>
            )}
            {NEXT_STATUS[task.status] ? (
              <button
                onClick={() => moveStatus(NEXT_STATUS[task.status]!)}
                disabled={moving}
                className="flex-1 rounded-md bg-slate-900 text-white text-sm font-medium py-2 hover:bg-slate-800 disabled:opacity-50"
              >
                {moving ? "Moving..." : `Move to ${STATUS_LABEL[NEXT_STATUS[task.status]!]} →`}
              </button>
            ) : (
              !PREV_STATUS[task.status] && (
                <p className="flex-1 text-xs text-emerald-600 text-center py-2">
                  This task is complete.
                </p>
              )
            )}
          </div>
        </div>
      </div>
    </div>
  );
}