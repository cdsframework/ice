#!/usr/bin/env python3
"""Convert Drools rule firings and working-memory changes to PlantUML."""

from __future__ import annotations

import argparse
import json
import re
import sys
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Iterable

LOG_LINE = re.compile(
    r"^(?P<timestamp>\d{4}-\d{2}-\d{2}T\S+)\s+.*?"
    r"drools-events\s+:\s+(?P<message>.*)$"
)
ANY_LOG_LINE = re.compile(r"^\d{4}-\d{2}-\d{2}T\S+\s+")
BEFORE_RULE = re.compile(r"^Before match fired:\s*(?P<rule>.*?)(?:\. Objects=|$)")
AFTER_RULE = re.compile(r"^After match fired:\s*(?P<rule>.*)$")
CHANGE = re.compile(
    r"^Object (?P<action>inserted|updated|deleted|retracted):\s*"
    r"(?P<object_type>[^-:]+?)\s+-\s+(?P<value>.*)$",
    re.IGNORECASE | re.DOTALL,
)


@dataclass
class StateChange:
    action: str
    object_type: str
    value: str
    timestamp: str


@dataclass
class RuleFiring:
    sequence: int
    rule: str
    timestamp: str
    changes: list[StateChange] = field(default_factory=list)
    completed: bool = False


def drools_records(lines: Iterable[str]) -> Iterable[tuple[str, str]]:
    """Combine a Drools log line and any multiline continuation lines."""
    timestamp: str | None = None
    parts: list[str] = []
    for raw_line in lines:
        line = raw_line.rstrip("\n")
        match = LOG_LINE.match(line)
        if match:
            if timestamp is not None:
                yield timestamp, "\n".join(parts)
            timestamp = match.group("timestamp")
            parts = [match.group("message")]
        elif ANY_LOG_LINE.match(line):
            if timestamp is not None:
                yield timestamp, "\n".join(parts)
            timestamp = None
            parts = []
        elif timestamp is not None:
            # A Java object's toString() output can span several physical lines.
            parts.append(line)
    if timestamp is not None:
        yield timestamp, "\n".join(parts)


def parse_trace(lines: Iterable[str]) -> tuple[list[RuleFiring], list[StateChange]]:
    firings: list[RuleFiring] = []
    outside_rule: list[StateChange] = []
    active: RuleFiring | None = None

    for timestamp, message in drools_records(lines):
        before = BEFORE_RULE.match(message)
        if before:
            active = RuleFiring(len(firings) + 1, before.group("rule"), timestamp)
            firings.append(active)
            continue

        after = AFTER_RULE.match(message)
        if after:
            if active is not None:
                active.completed = True
                active = None
            continue

        change_match = CHANGE.match(message)
        if change_match:
            change = StateChange(
                action=change_match.group("action").lower(),
                object_type=change_match.group("object_type").strip(),
                value=change_match.group("value").strip(),
                timestamp=timestamp,
            )
            (active.changes if active is not None else outside_rule).append(change)

    return firings, outside_rule


def shorten(value: str, max_length: int) -> str:
    value = " ".join(value.split())
    if max_length and len(value) > max_length:
        return value[: max_length - 1] + "…"
    return value


def plantuml_text(value: str) -> str:
    """Keep log content from being interpreted as PlantUML/Creole markup."""
    return (
        value.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(";", "&#59;")
        .replace("{", "&#123;")
        .replace("}", "&#125;")
    )


def print_plantuml(
    firings: list[RuleFiring], outside_rule: list[StateChange], max_length: int
) -> None:
    print("@startuml")
    print("title Drools Rule Execution Trace")
    print("skinparam shadowing false")
    print("skinparam activity {")
    print("  BackgroundColor #EAF2F8")
    print("  BorderColor #2471A3")
    print("}")
    print("skinparam note {")
    print("  BackgroundColor #FFFDE7")
    print("  BorderColor #B7950B")
    print("}")
    print("start")

    if outside_rule:
        print("note right")
        print(f"Working-memory changes outside rule firings ({len(outside_rule)})")
        for change in outside_rule:
            detail = plantuml_text(shorten(change.value, max_length))
            object_type = plantuml_text(change.object_type)
            print(f"{change.timestamp}  **{change.action.upper()}** {object_type}: {detail}")
        print("end note")

    for firing in firings:
        suffix = " [INCOMPLETE]" if not firing.completed else ""
        rule = plantuml_text(firing.rule)
        print(f":**{firing.sequence:04d}** {rule}\\n{firing.timestamp}{suffix};")
        if firing.changes:
            print("note right")
            for change in firing.changes:
                detail = plantuml_text(shorten(change.value, max_length))
                object_type = plantuml_text(change.object_type)
                print(f"**{change.action.upper()}** {object_type}: {detail}")
            print("end note")

    print("stop")
    print("@enduml")


def main() -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Generate a PlantUML activity diagram showing fired Drools rules in "
            "order and their working-memory state changes."
        )
    )
    parser.add_argument("log", type=Path, help="application log to parse (for example, test.log)")
    parser.add_argument(
        "--max-length",
        type=int,
        default=240,
        metavar="N",
        help="truncate each object snapshot to N characters; use 0 for no limit (default: 240)",
    )
    parser.add_argument("--json", action="store_true", help="emit machine-readable JSON")
    args = parser.parse_args()

    if args.max_length < 0:
        parser.error("--max-length must be zero or greater")

    try:
        with args.log.open(encoding="utf-8", errors="replace") as log_file:
            firings, outside_rule = parse_trace(log_file)
    except OSError as error:
        print(f"error: cannot read {args.log}: {error}", file=sys.stderr)
        return 1

    if args.json:
        json.dump(
            {
                "rule_firings": [asdict(firing) for firing in firings],
                "changes_outside_rule_firings": [asdict(change) for change in outside_rule],
            },
            sys.stdout,
            indent=2,
        )
        print()
    else:
        print_plantuml(firings, outside_rule, args.max_length)
    return 0


if __name__ == "__main__":
    main()
