import { useEffect, useState } from "react";
import { getResources, searchResources } from "../api/resourceApi";

function ResourcePage() {
  const [resources, setResources] = useState([]);
  const [keyword, setKeyword] = useState("");

  const loadResources = async () => {
    try {
      const data = await getResources();
      setResources(Array.isArray(data) ? data : []);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    loadResources();
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();

    if (!keyword.trim()) {
      loadResources();
      return;
    }

    try {
      const data = await searchResources(keyword);
      setResources(Array.isArray(data) ? data : []);
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Resource Finder</h1>
        <p>Search for food, housing, healthcare, transportation, and support services.</p>
      </div>

      <form className="search-card" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Search food, housing, health..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />

        <button type="submit">Search</button>
      </form>

      <div className="resource-grid">
        {resources.map((resource, index) => (
          <div className="resource-card" key={resource.id || index}>
            <span>{resource.category || "Resource"}</span>
            <h3>{resource.name || resource.title}</h3>
            <p>{resource.description}</p>
            <small>{resource.phone || resource.website || resource.location}</small>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ResourcePage;
