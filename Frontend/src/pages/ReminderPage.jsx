import { useEffect, useState } from "react";
import {
    createReminder,
    getReminders,
    sendSupportRequest
} from "../api.js";

export default function ReminderPage() {
    const [reminders, setReminders] = useState([]);
    const [form, setForm] = useState({
        title: "",
        type: "Appointment",
        date: "",
        time: ""
    });

    useEffect(() => {
        async function load() {
            const data = await getReminders();
            setReminders(data);
        }

        load();
    }, []);

    function update(field, value) {
        setForm((current) => ({ ...current, [field]: value }));
    }

    async function handleCreate(event) {
        event.preventDefault();

        if (!form.title || !form.date || !form.time) return;

        const saved = await createReminder(form);
        setReminders((current) => [saved, ...current]);

        setForm({
            title: "",
            type: "Appointment",
            date: "",
            time: ""
        });
    }

    async function requestSupport(reminder) {
        const result = await sendSupportRequest({
            sourceProject: "KindConnect",
            userId: 1,
            category: "Reminder Help",
            resourceTitle: reminder.title,
            supportType: reminder.type,
            message: `The user needs help with reminder: ${reminder.title}`
        });

        alert(`Support request sent to ${result.routedChannel}`);
    }

    return (
        <div>
            <h1>Reminders</h1>
            <p className="muted">KindConnect reminders for appointments and self-care.</p>

            <form onSubmit={handleCreate} className="formGrid">
                <input
                    placeholder="Title"
                    value={form.title}
                    onChange={(event) => update("title", event.target.value)}
                />

                <select
                    value={form.type}
                    onChange={(event) => update("type", event.target.value)}
                >
                    <option>Appointment</option>
                    <option>Medicine</option>
                    <option>Water</option>
                    <option>Bill</option>
                    <option>Self-care</option>
                </select>

                <input
                    type="date"
                    value={form.date}
                    onChange={(event) => update("date", event.target.value)}
                />

                <input
                    type="time"
                    value={form.time}
                    onChange={(event) => update("time", event.target.value)}
                />

                <button className="btn" type="submit">
                    Add
                </button>
            </form>

            {reminders.map((reminder) => (
                <div key={reminder.id} className="card row">
                    <div>
                        <h3>{reminder.title}</h3>
                        <p>
                            {reminder.type} • {reminder.date} at {reminder.time}
                        </p>
                        <p>Status: {reminder.completed ? "Completed" : "Pending"}</p>
                    </div>

                    <button className="btn" onClick={() => requestSupport(reminder)}>
                        Request Support
                    </button>
                </div>
            ))}
        </div>
    );
}