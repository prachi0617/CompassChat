import { useEffect, useState } from "react";
import { getReminders, createReminder } from "../api/reminderApi.js";

const REMINDER_TYPES = [
    "APPOINTMENT",
    "MEDICINE",
    "WATER",
    "BILL",
    "SELF_CARE",
    "OTHER"
];

export default function ReminderPage() {
    const [reminders, setReminders] = useState([]);
    const [title, setTitle] = useState("");
    const [type, setType] = useState("APPOINTMENT");
    const [date, setDate] = useState("");
    const [time, setTime] = useState("");

    useEffect(() => {
        loadReminders();
    }, []);

    async function loadReminders() {
        try {
            const data = await getReminders();
            setReminders(data);
        } catch {
            setReminders([]);
        }
    }

    async function handleCreate(event) {
        event.preventDefault();
        if (!title || !date || !time) return;

        try {
            const saved = await createReminder({ title, type, date, time });
            setReminders((current) => [saved, ...current]);
            setTitle("");
            setType("APPOINTMENT");
            setDate("");
            setTime("");
        } catch {
            // silently fail
        }
    }

    return (
        <div>
            <h1>Reminders</h1>
            <p className="muted">
                Manage appointments, medication, self-care, and more.
            </p>

            <form onSubmit={handleCreate} className="formGrid">
                <input
                    placeholder="Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />

                <select value={type} onChange={(e) => setType(e.target.value)}>
                    {REMINDER_TYPES.map((t) => (
                        <option key={t} value={t}>
                            {t.replace("_", " ").toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase())}
                        </option>
                    ))}
                </select>

                <input
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                />

                <input
                    type="time"
                    value={time}
                    onChange={(e) => setTime(e.target.value)}
                />

                <button className="btn" type="submit">
                    Add
                </button>
            </form>

            {reminders.length === 0 && (
                <p className="muted">No reminders yet.</p>
            )}

            {reminders.map((reminder) => (
                <div key={reminder.id} className="card row">
                    <div>
                        <span className="badge">{reminder.type}</span>
                        <h3>{reminder.title}</h3>
                        <p>
                            {reminder.date} at {reminder.time}
                        </p>
                        <p>
                            Status:{" "}
                            <strong>
                                {reminder.completed ? "Completed" : "Pending"}
                            </strong>
                        </p>
                    </div>
                </div>
            ))}
        </div>
    );
}
