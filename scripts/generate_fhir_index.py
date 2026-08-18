#!/usr/bin/env python3
"""Generate an HTML index for all public FHIR artifacts."""

from __future__ import annotations

import html
import json
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
FHIR_DIR = REPO_ROOT / "fhir"
ICE_DIR = FHIR_DIR / "ice"
OUTPUT = FHIR_DIR / "index.html"
EXTRA_ARTIFACTS = [
    FHIR_DIR / "PlanDefinition/ice-forecast.json",
]


def load_metadata(path: Path) -> dict[str, str]:
    if path.suffix != ".json":
        return {}

    try:
        data = json.loads(path.read_text())
    except json.JSONDecodeError:
        return {"resourceType": "Invalid JSON"}

    return {
        "resourceType": str(data.get("resourceType", "")),
        "title": str(data.get("title") or data.get("name") or data.get("id") or ""),
        "description": str(data.get("description") or data.get("purpose") or ""),
        "version": str(data.get("version", "")),
        "status": str(data.get("status", "")),
        "url": str(data.get("url", "")),
    }


def is_code_system_artifact(path: Path) -> bool:
    if path.suffix != ".json":
        return False

    try:
        data = json.loads(path.read_text())
    except json.JSONDecodeError:
        return False

    if data.get("resourceType") == "CodeSystem":
        return True

    if data.get("resourceType") != "Bundle":
        return False

    entries = data.get("entry") or []
    return bool(entries) and all(
        entry.get("resource", {}).get("resourceType") == "CodeSystem"
        for entry in entries
    )


def render_status(status: str) -> str:
    if not status:
        return ""
    return f'<span class="status">{html.escape(status)}</span>'


def render_index(files: list[Path]) -> str:
    rows = []
    for path in files:
        rel = path.relative_to(FHIR_DIR).as_posix()
        meta = load_metadata(path)
        file_type = meta.get("resourceType") or path.suffix.removeprefix(".").upper() or "File"
        rows.append(
            "          <tr>\n"
            f'            <td><a href="{html.escape(rel)}">{html.escape(rel)}</a></td>\n'
            f"            <td>{html.escape(file_type)}</td>\n"
            f"            <td>{html.escape(meta.get('title', ''))}</td>\n"
            f"            <td>{html.escape(meta.get('description', ''))}</td>\n"
            f"            <td class=\"version\">{html.escape(meta.get('version', ''))}</td>\n"
            f"            <td>{render_status(meta.get('status', ''))}</td>\n"
            f"            <td><code>{html.escape(meta.get('url', ''))}</code></td>\n"
            "          </tr>"
        )

    return f"""<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>ICE FHIR Artifacts</title>
  <style>
    :root {{
      color-scheme: dark;
      --bg: #101318;
      --panel: #171c23;
      --text: #edf2f7;
      --muted: #a8b3c2;
      --border: #2b3441;
      --header: #202733;
      --link: #7cc4ff;
      --status-bg: #163524;
      --status-text: #80e0a7;
    }}

    * {{ box-sizing: border-box; }}

    body {{
      margin: 0;
      background: var(--bg);
      color: var(--text);
      font-family: Arial, Helvetica, sans-serif;
      line-height: 1.45;
    }}

    main {{
      width: min(1240px, calc(100% - 32px));
      margin: 32px auto;
    }}

    header {{ margin-bottom: 20px; }}

    h1 {{
      margin: 0 0 6px;
      font-size: 28px;
      font-weight: 700;
      letter-spacing: 0;
    }}

    .summary {{
      margin: 0;
      color: var(--muted);
      font-size: 15px;
    }}

    .nav {{
      margin: 18px 0;
    }}

    .nav a {{
      display: inline-block;
      padding: 8px 0;
    }}

    .table-wrap {{
      overflow-x: auto;
      background: var(--panel);
      border: 1px solid var(--border);
      border-radius: 8px;
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.28);
    }}

    table {{
      width: 100%;
      border-collapse: collapse;
      min-width: 1100px;
    }}

    th,
    td {{
      padding: 12px 14px;
      border-bottom: 1px solid var(--border);
      text-align: left;
      vertical-align: top;
      font-size: 14px;
    }}

    th {{
      position: sticky;
      top: 0;
      background: var(--header);
      color: #c7d2e0;
      font-size: 12px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      white-space: nowrap;
    }}

    tr:last-child td {{ border-bottom: 0; }}

    a {{
      color: var(--link);
      font-weight: 700;
      text-decoration: none;
    }}

    a:hover,
    a:focus {{ text-decoration: underline; }}

    code {{
      color: #cbd5e1;
      font-family: Consolas, Monaco, monospace;
      font-size: 13px;
      word-break: break-all;
    }}

    .status {{
      display: inline-block;
      min-width: 54px;
      padding: 2px 8px;
      border-radius: 999px;
      background: var(--status-bg);
      color: var(--status-text);
      font-size: 12px;
      font-weight: 700;
      text-align: center;
    }}

    .version {{ white-space: nowrap; }}
  </style>
</head>
<body>
  <main>
    <header>
      <h1>ICE FHIR Artifacts</h1>
      <p class="summary">Public non-CodeSystem FHIR artifacts under the local <code>fhir/ice/</code> directory.</p>
    </header>

    <nav class="nav" aria-label="Related indexes">
      <a href="code-systems.html">code-systems.html</a>
    </nav>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>File</th>
            <th>Type</th>
            <th>Title</th>
            <th>Description</th>
            <th>Version</th>
            <th>Status</th>
            <th>Canonical URL</th>
          </tr>
        </thead>
        <tbody>
{chr(10).join(rows)}
        </tbody>
      </table>
    </div>
  </main>
</body>
</html>
"""


def main() -> None:
    files = [
        path
        for path in ICE_DIR.rglob("*")
        if path.is_file()
        and not is_code_system_artifact(path)
        and not any(part.startswith(".") for part in path.relative_to(FHIR_DIR).parts)
    ]
    files.extend(
        path
        for path in EXTRA_ARTIFACTS
        if path.is_file() and not is_code_system_artifact(path)
    )
    files = sorted(set(files))
    OUTPUT.write_text(render_index(files))
    print(f"{OUTPUT.relative_to(REPO_ROOT)}: {len(files)} files")


if __name__ == "__main__":
    main()
