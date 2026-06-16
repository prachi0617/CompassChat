import { useEffect, useState } from "react";
import {
  getReminders,
  createReminder,
  completeReminder,
} from "../api/reminderApi";

function ReminderPage() {
  const [reminders, setReminders] = useState([]);

  const [form, setForm] = useState({
    title: "",
    type: "MEDICINE",
    date: "",
    time: "",
  });

  const loadReminders = async () => {
    try {
      const data = await getReminders();
      setReminders(Array.isArray(data) ? data : []);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    loadReminders();
  }, []);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleCreate = async (e) => {
    e.preventDefault();

    const userId = localStorage.getItem("userId") || 1;

    const payload = {
      ...form,
      userId: Number(userId),
    };

    await createReminder(payload);

    setForm({
      title: "",
      type: "MEDICINE",
      date: "",
      time: "",
    });

    loadReminders();
  };

  const handleComplete = async (id) => {
    await completeReminder(id);
    loadReminders();
  };

  const pending = reminders.filter((r) => !r.completed);
  const completed = reminders.filter((r) => r.completed);

  return (
    <div>
      <div className="page-header">
        <h1>Reminders</h1>
        <p>Create reminders for medicine, appointments, water, bills, and self-care.</p>
      </div>

      <form className="form-card" onSubmit={handleCreate}>
        <input
          type="text"
          name="title"
          placeholder="Reminder title"
          value={form.title}
          onChange={handleChange}
          required
        />

        <select name="type" value={form.type} onChange={handleChange}>
          <option value="MEDICINE">Medicine</option>
          <option value="APPOINTMENT">Appointment</option>
          <option value="WATER">Water</option>
          <option value="BILL">Bill</option>
          <option value="SELF_CARE">Self Care</option>
        </select>

        <input type="date" name="date" value={form.date} onChange={handleChange} required />
        <input type="time" name="time" value={form.time} onChange={handleChange} required />

        <button type="submit">Add Reminder</button>
      </form>

      <h2 className="section-title">Pending</h2>

      <div className="list">
        {pending.map((reminder) => (
          <div className="list-card" key={reminder.id}>
            <div>
              <h3>{reminder.title}</h3>
              <p>
                {reminder.type} • {reminder.date} • {reminder.time}
              </p>
            </div>

            <button onClick={() => handleComplete(reminder.id)}>Complete</button>
          </div>
        ))}
      </div>

      <h2 className="section-title">Completed</h2>

      <div className="list">
        {completed.map((reminder) => (
          <div className="list-card completed" key={reminder.id}>
            <div>
              <h3>{reminder.title}</h3>
              <p>
                {reminder.type} • {reminder.date} • {reminder.time}
              </p>
            </div>

            <span>Done</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ReminderPage;
