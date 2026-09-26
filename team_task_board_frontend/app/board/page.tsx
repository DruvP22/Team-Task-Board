"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { api, Task } from "@/lib/api";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const COLUMNS: Task["status"][] = ["BACKLOG", "IN_PROGRESS", "REVIEW", "DONE"];

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

const STATUS_STYLE: Record<Task["status"], { bar: string; dot: string; text: string }> = {
  BACKLOG: { bar: "bg-slate-400", dot: "bg-slate-400", text: "text-slate-600" },
  IN_PROGRESS: { bar: "bg-sky-500", dot: "bg-sky-500", text: "text-sky-600" },
  REVIEW: { bar: "bg-violet-500", dot: "bg-violet-500", text: "text-violet-600" },
  DONE: { bar: "bg-emerald-500", dot: "bg-emerald-500", text: "text-emerald-600" },
};

const PRIORITY_STYLE: Record<Task["priority"], string> = {
  LOW: "bg-slate-100 text-slate-600",
  MEDIUM: "bg-amber-100 text-amber-700",
  HIGH: "bg-rose-100 text-rose-700",
};

type SortOption = "newest" | "oldest" | "priority";

const PRIORITY_WEIGHT: Record<Task["priority"], number> = {
  HIGH: 0,
  MEDIUM: 1,
  LOW: 2,
};

function sortTasks(tasks: Task[], sortBy: SortOption): Task[] {
  const copy = [...tasks];
  switch (sortBy) {
    case "newest":
      return copy.sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
    case "oldest":
      return copy.sort(
        (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
      );
    case "priority":
      return copy.sort((a, b) => PRIORITY_WEIGHT[a.priority] - PRIORITY_WEIGHT[b.priority]);
  }
}

export default function BoardPage() {
  const router = useRouter();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [movingId, setMovingId] = useState<number | null>(null);
  const [sortBy, setSortBy] = useState<Record<Task["status"], SortOption>>({
    BACKLOG: "newest",
    IN_PROGRESS: "newest",
    REVIEW: "newest",
    DONE: "newest",
  });

  useEffect(() => {
    loadTasks();
  }, []);

  async function loadTasks() {
    setLoading(true);
    try {
      const data = await api.getTasks(0, 100);
      setTasks(data.content);
    } catch (err) {
      console.error("Failed to load tasks", err);
    } finally {
      setLoading(false);
    }
  }

  async function moveTask(task: Task, newStatus: Task["status"]) {
    setMovingId(task.id);
    try {
      await api.changeTaskStatus(task.id, newStatus);
      await loadTasks();
    } catch (err) {
      alert(err instanceof Error ? err.message : "Failed to update status");
    } finally {
      setMovingId(null);
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-slate-400">
        Loading board...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Top bar */}
      <div className="border-b border-slate-200 bg-white">
        <div className="mx-auto max-w-6xl px-6 py-5 flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold tracking-tight text-slate-900">
              Team Task Board
            </h1>
            <p className="text-sm text-slate-400">
              {tasks.length} task{tasks.length !== 1 ? "s" : ""} across the workflow
            </p>
          </div>
          <Button onClick={() => router.push("/board/new")}>New task</Button>
        </div>

        {/* Sequence rail — the four stages, since this genuinely is a pipeline */}
        <div className="mx-auto max-w-6xl px-6 pb-4">
          <div className="flex gap-1">
            {COLUMNS.map((status) => (
              <div
                key={status}
                className={`h-1 flex-1 rounded-full ${STATUS_STYLE[status].bar} opacity-70`}
              />
            ))}
          </div>
        </div>
      </div>

      {/* Columns */}
      <div className="mx-auto max-w-6xl px-6 py-8">
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {COLUMNS.map((status) => {
            const columnTasks = sortTasks(
              tasks.filter((t) => t.status === status),
              sortBy[status]
            );
            const style = STATUS_STYLE[status];

            return (
              <div key={status} className="rounded-lg bg-white border border-slate-200">
                <div className={`h-1 rounded-t-lg ${style.bar}`} />
                <div className="p-4">
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <span className={`h-1.5 w-1.5 rounded-full ${style.dot}`} />
                      <h2 className="text-sm font-medium text-slate-700">
                        {STATUS_LABEL[status]}
                      </h2>
                      <span className="text-xs text-slate-400">{columnTasks.length}</span>
                    </div>
                    <Select
                      value={sortBy[status]}
                      onValueChange={(value: SortOption) =>
                        setSortBy((prev) => ({ ...prev, [status]: value }))
                      }
                    >
                      <SelectTrigger className="h-6 w-[88px] text-xs border-none shadow-none text-slate-400">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="newest">Newest</SelectItem>
                        <SelectItem value="oldest">Oldest</SelectItem>
                        <SelectItem value="priority">Priority</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    {columnTasks.length === 0 && (
                      <p className="text-xs text-slate-300 py-6 text-center">
                        Nothing here yet
                      </p>
                    )}

                    {columnTasks.map((task) => (
                      <div
                        key={task.id}
                        onClick={() => router.push(`/tasks/${task.id}`)}
                        className={`group cursor-pointer rounded-md border border-slate-100 p-3 transition-all hover:border-slate-300 hover:shadow-sm ${
                          movingId === task.id ? "opacity-40" : ""
                        }`}
                      >
                        <p className="text-sm font-medium text-slate-800 mb-2">
                          {task.title}
                        </p>

                        <div className="flex items-center justify-between">
                          <span
                            className={`text-[11px] px-1.5 py-0.5 rounded ${PRIORITY_STYLE[task.priority]}`}
                          >
                            {task.priority}
                          </span>
                          {task.assigneeEmail && (
                            <span className="text-[11px] text-slate-400 truncate max-w-[100px]">
                              {task.assigneeEmail}
                            </span>
                          )}
                        </div>

                        <div className="mt-2 flex gap-3 opacity-0 group-hover:opacity-100 transition-opacity">
                          {PREV_STATUS[task.status] && (
                            <button
                              onClick={(e) => {
                                e.stopPropagation();
                                moveTask(task, PREV_STATUS[task.status]!);
                              }}
                              className="text-[11px] font-medium text-slate-400 hover:text-slate-600"
                            >
                              ← {STATUS_LABEL[PREV_STATUS[task.status]!]}
                            </button>
                          )}
                          {NEXT_STATUS[task.status] && (
                            <button
                              onClick={(e) => {
                                e.stopPropagation();
                                moveTask(task, NEXT_STATUS[task.status]!);
                              }}
                              className={`text-[11px] font-medium ${style.text}`}
                            >
                              {STATUS_LABEL[NEXT_STATUS[task.status]!]} →
                            </button>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}