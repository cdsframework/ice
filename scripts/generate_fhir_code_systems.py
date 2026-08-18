#!/usr/bin/env python3
"""Generate public FHIR CodeSystem JSON artifacts from ICE supporting-data YAML."""

from __future__ import annotations

import html
import json
import re
from urllib.parse import urlparse
from collections import OrderedDict
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
SOURCE_DIRS = [
    REPO_ROOT
    / "opencds-decision-support-service/src/main/resources/data/knowledgeCommon/org.cdsframework.ice/ice-supporting-data",
    REPO_ROOT
    / "opencds-decision-support-service/src/main/resources/data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data",
]
FHIR_DIR = REPO_ROOT / "fhir"
INDEX_PATH = FHIR_DIR / "code-systems.html"
TERMINOLOGY_HOST = "terminology.cdsframework.org"
ICE_PATH_PREFIX = "ice/"


def parse_scalar(value: str):
    value = value.rstrip()
    if len(value) >= 2 and value[0] == value[-1] and value[0] in ("'", '"'):
        return value[1:-1]
    if value == "true":
        return True
    if value == "false":
        return False
    if re.fullmatch(r"-?\d+", value):
        return int(value)
    return value


def filename_from_url(url: str) -> str:
    return f"{url.rstrip('/').rsplit('/', 1)[-1]}.json"


def artifact_path_from_url(url: str) -> Path:
    parsed = urlparse(url)
    path = parsed.path.split("|", 1)[0].strip("/")
    if parsed.netloc == TERMINOLOGY_HOST:
        relative = path
    else:
        relative = f"{parsed.netloc}/{path}".strip("/")
    return FHIR_DIR / f"{relative}.json"


def is_ice_url(url: str) -> bool:
    parsed = urlparse(url)
    return parsed.netloc == TERMINOLOGY_HOST and parsed.path.strip("/").startswith(ICE_PATH_PREFIX)


def id_from_filename(filename: str) -> str:
    return filename.removesuffix(".json")


def fhir_name(filename: str) -> str:
    words = re.split(r"[^A-Za-z0-9]+", filename.removesuffix(".json"))
    return "".join(word[:1].upper() + word[1:] for word in words if word)


def append_continuation(target, key: str, text: str) -> None:
    if target is not None and key in target and isinstance(target[key], str):
        target[key] = f"{target[key]} {text.strip()}".strip()


def parse_code_system(path: Path):
    lines = path.read_text().splitlines()
    code_system = None
    identifier = None
    top_property = None
    concept = None
    concept_property = None
    value_coding = None
    last_target = None
    last_key = None
    in_identifier = False
    in_top_properties = False
    in_concepts = False

    for raw_line in lines:
        if not raw_line.strip() or raw_line.lstrip().startswith("#"):
            continue

        indent = len(raw_line) - len(raw_line.lstrip(" "))
        text = raw_line.strip()

        if indent == 8 and text.endswith(":"):
            code_system = OrderedDict()
            code_system["sourceName"] = text[:-1]
            identifier = None
            top_property = None
            concept = None
            concept_property = None
            value_coding = None
            in_identifier = False
            in_top_properties = False
            in_concepts = False
            continue

        if code_system is None:
            continue

        if indent == 10 and ": " in text:
            key, value = text.split(": ", 1)
            if key in ("name", "description", "url", "title", "version", "status", "content"):
                code_system[key] = parse_scalar(value)
                last_target, last_key = code_system, key
            continue

        if indent == 10 and text == "identifier:":
            code_system["identifier"] = []
            in_identifier = True
            in_top_properties = False
            in_concepts = False
            last_target = None
            last_key = None
            continue

        if indent == 10 and text == "property:":
            code_system["property"] = []
            in_identifier = False
            in_top_properties = True
            in_concepts = False
            last_target = None
            last_key = None
            continue

        if indent == 10 and text == "concept:":
            code_system["concept"] = []
            in_identifier = False
            in_top_properties = False
            in_concepts = True
            last_target = None
            last_key = None
            continue

        if in_identifier:
            if indent == 12 and text.startswith("- "):
                identifier = OrderedDict()
                code_system["identifier"].append(identifier)
                item = text[2:]
                if ": " in item:
                    key, value = item.split(": ", 1)
                    identifier[key] = parse_scalar(value)
                    last_target, last_key = identifier, key
                continue
            if indent == 14 and identifier is not None and ": " in text:
                key, value = text.split(": ", 1)
                identifier[key] = parse_scalar(value)
                last_target, last_key = identifier, key
                continue

        if in_top_properties and not in_concepts:
            if indent == 12 and text.startswith("- "):
                top_property = OrderedDict()
                code_system["property"].append(top_property)
                item = text[2:]
                if ": " in item:
                    key, value = item.split(": ", 1)
                    top_property[key] = parse_scalar(value)
                    last_target, last_key = top_property, key
                continue
            if indent == 14 and top_property is not None and ": " in text:
                key, value = text.split(": ", 1)
                top_property[key] = parse_scalar(value)
                last_target, last_key = top_property, key
                continue

        if in_concepts:
            if indent == 12 and text.startswith("- "):
                concept = OrderedDict()
                code_system["concept"].append(concept)
                concept_property = None
                value_coding = None
                item = text[2:]
                if ": " in item:
                    key, value = item.split(": ", 1)
                    concept[key] = parse_scalar(value)
                    last_target, last_key = concept, key
                continue
            if indent == 14 and text == "property:":
                concept.setdefault("property", [])
                concept_property = None
                value_coding = None
                last_target = None
                last_key = None
                continue
            if indent == 14 and concept is not None and ": " in text:
                key, value = text.split(": ", 1)
                concept[key] = parse_scalar(value)
                last_target, last_key = concept, key
                continue
            if indent == 16 and text.startswith("- "):
                concept_property = OrderedDict()
                concept.setdefault("property", []).append(concept_property)
                value_coding = None
                item = text[2:]
                if ": " in item:
                    key, value = item.split(": ", 1)
                    concept_property[key] = parse_scalar(value)
                    last_target, last_key = concept_property, key
                continue
            if indent == 18 and concept_property is not None:
                if text == "valueCoding:":
                    value_coding = OrderedDict()
                    concept_property["valueCoding"] = value_coding
                    last_target = None
                    last_key = None
                    continue
                if ": " in text:
                    key, value = text.split(": ", 1)
                    concept_property[key] = parse_scalar(value)
                    last_target, last_key = concept_property, key
                    continue
            if indent == 20 and value_coding is not None and ": " in text:
                key, value = text.split(": ", 1)
                value_coding[key] = parse_scalar(value)
                last_target, last_key = value_coding, key
                continue

        if indent > 0 and last_target is not None and last_key is not None:
            append_continuation(last_target, last_key, text)

    return code_system


def merge_unique_list(existing, incoming):
    merged = list(existing or [])
    seen = {json.dumps(item, sort_keys=True) for item in merged}
    for item in incoming or []:
        key = json.dumps(item, sort_keys=True)
        if key not in seen:
            merged.append(item)
            seen.add(key)
    return merged


def merge_concepts(existing, incoming):
    by_code = OrderedDict()
    for concept in existing or []:
        by_code[concept.get("code")] = OrderedDict(concept)

    for concept in incoming or []:
        code = concept.get("code")
        if code not in by_code:
            by_code[code] = OrderedDict(concept)
            continue

        target = by_code[code]
        for key in ("display", "definition"):
            if key not in target and key in concept:
                target[key] = concept[key]
        target["property"] = merge_unique_list(target.get("property"), concept.get("property"))
        if not target.get("property"):
            target.pop("property", None)

    return list(by_code.values())


def collect_code_systems():
    by_url = OrderedDict()

    for source_dir in SOURCE_DIRS:
        for path in sorted(source_dir.glob("*.yml")):
            if path.name == "iceSupportingData.yml":
                continue

            code_system = parse_code_system(path)
            if not code_system:
                continue

            url = code_system["url"]
            if not is_ice_url(url):
                continue

            source = str(path.relative_to(REPO_ROOT))
            if url not in by_url:
                code_system["sources"] = [source]
                by_url[url] = code_system
                continue

            target = by_url[url]
            target["sources"].append(source)
            target["identifier"] = merge_unique_list(target.get("identifier"), code_system.get("identifier"))
            target["property"] = merge_unique_list(target.get("property"), code_system.get("property"))
            target["concept"] = merge_concepts(target.get("concept"), code_system.get("concept"))

    return by_url


def to_fhir_resource(code_system):
    filename = filename_from_url(code_system["url"])
    resource = OrderedDict()
    resource["resourceType"] = "CodeSystem"
    resource["id"] = id_from_filename(filename)
    resource["url"] = code_system["url"]
    resource["version"] = str(code_system.get("version", "1.0.0"))
    resource["name"] = fhir_name(filename)
    if code_system.get("title"):
        resource["title"] = code_system["title"]
    resource["status"] = code_system.get("status", "active")
    if code_system.get("description"):
        resource["description"] = code_system["description"]
    resource["content"] = code_system.get("content", "complete")
    resource["caseSensitive"] = True
    if code_system.get("property"):
        resource["property"] = code_system["property"]
    resource["concept"] = code_system.get("concept", [])
    return filename, resource


def render_index(resources):
    rows = []
    for filename, resource in sorted(resources, key=lambda item: item[0].lower()):
        rows.append(
            "          <tr>\n"
            f'            <td><a href="{html.escape(filename)}">{html.escape(filename)}</a></td>\n'
            f"            <td>{html.escape(resource.get('title', ''))}</td>\n"
            f"            <td>{html.escape(resource.get('description', ''))}</td>\n"
            f"            <td class=\"version\">{html.escape(resource.get('version', ''))}</td>\n"
            f"            <td><span class=\"status\">{html.escape(resource.get('status', ''))}</span></td>\n"
            f"            <td class=\"count\">{len(resource.get('concept', []))}</td>\n"
            f"            <td><code>{html.escape(resource.get('url', ''))}</code></td>\n"
            "          </tr>"
        )

    return f"""<!doctype html>
<html lang=\"en\">
<head>
  <meta charset=\"utf-8\">
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  <title>ICE CodeSystem References</title>
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
      width: min(1180px, calc(100% - 32px));
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
      min-width: 920px;
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

    .count,
    .version {{ white-space: nowrap; }}
  </style>
</head>
<body>
  <main>
    <header>
      <h1>ICE CodeSystem References</h1>
      <p class=\"summary\">Standalone ICE-owned FHIR CodeSystem JSON files generated from the common ICE and NYC CIR ICE supporting data.</p>
    </header>

    <div class=\"table-wrap\">
      <table>
        <thead>
          <tr>
            <th>File</th>
            <th>Title</th>
            <th>Description</th>
            <th>Version</th>
            <th>Status</th>
            <th>Concepts</th>
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
    code_systems = list(collect_code_systems().values())

    old_output_dir = FHIR_DIR / "code-systems"
    if old_output_dir.exists():
        for stale_json in old_output_dir.glob("*.json"):
            stale_json.unlink()
        old_index = old_output_dir / "code-systems.html"
        if old_index.exists():
            old_index.unlink()

    for code_system in code_systems:
        stale_json = artifact_path_from_url(code_system["url"])
        if stale_json.exists():
            stale_json.unlink()

    resources = []
    for code_system in code_systems:
        filename, resource = to_fhir_resource(code_system)
        output_path = artifact_path_from_url(resource["url"])
        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_text(json.dumps(resource, indent=2) + "\n")
        resources.append((output_path.relative_to(FHIR_DIR).as_posix(), resource))

    INDEX_PATH.write_text(render_index(resources))

    for filename, resource in sorted(resources, key=lambda item: item[0].lower()):
        concept_count = len(resource.get("concept", []))
        print(f"{filename}: {concept_count} concepts")


if __name__ == "__main__":
    main()
