import { useEffect, useState } from "react";
import { createMood, getMoods } from "../api/moodApi";

function MoodPage() {
  const [moods, setMoods] = useState([]);

  const [form, setForm] = useState({
    mood: "HAPPY",
    note: "",
    date: new Date().toISOString().split("T")[0],
  });

  const loadMoods = async () => {
    try {
      const data = await getMoods();
      setMoods(Array.isArray(data) ? data : []);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    loadMoods();
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

    await createMood(payload);

    setForm({
      mood: "HAPPY",
      note: "",
      date: new Date().toISOString().split("T")[0],
    });

    loadMoods();
  };

  return (
    <div>
      <div className="page-header">
        <h1>Mood Check-In</h1>
        <p>Track your mood and receive better support.</p>
      </div>

      <form className="form-card" onSubmit={handleCreate}>
        <select name="mood" value={form.mood} onChange={handleChange}>
          <option value="HAPPY">Happy</option>
          <option value="SAD">Sad</option>
          <option value="ANXIOUS">Anxious</option>
          <option value="LONELY">Lonely</option>
          <option value="STRESSED">Stressed</option>
          <option value="CALM">Calm</option>
        </select>

        <input
          type="text"
          name="note"
          placeholder="Write a short note..."
          value={form.note}
          onChange={handleChange}
        />

        <input type="date" name="date" value={form.date} onChange={handleChange} />

        <button type="submit">Save Mood</button>
      </form>

      <div className="list">
        {moods.map((mood) => (
          <div className="list-card" key={mood.id}>
            <div>
              <h3>{mood.mood}</h3>
              <p>{mood.note}</p>
              <small>{mood.date}</small>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MoodPage;
