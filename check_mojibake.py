import sys
import pathlib

MOJIBAKE_SIGNS = [
    "鍙",
    "鏀",
    "鐢",
    "鈥",
    "甯",
    "閫",
    "\ufffd",
    "Ã",
    "Ð",
    "ï»¿",
]


def safe_print(text: str):
    try:
        print(text)
    except UnicodeEncodeError:
        sys.stdout.buffer.write((text + "\n").encode("utf-8", errors="backslashreplace"))


def check_file(path: pathlib.Path):
    hits = []
    try:
        raw = path.read_bytes()
    except Exception as exc:
        return [(-1, f"[READ_ERROR] {exc}")]

    if raw.startswith(b"\xef\xbb\xbf"):
        hits.append((1, "[BOM] UTF-8 BOM detected"))

    try:
        text = raw.decode("utf-8")
    except UnicodeDecodeError as exc:
        return [(-1, f"[DECODE_ERROR] {exc}")]

    for idx, line in enumerate(text.splitlines(), start=1):
        if any(sign in line for sign in MOJIBAKE_SIGNS):
            hits.append((idx, line))
    return hits


def main():
    if len(sys.argv) < 2:
        safe_print("Usage: python check_mojibake.py <file1> [file2 ...]")
        return 2

    total_hits = 0
    for arg in sys.argv[1:]:
        p = pathlib.Path(arg)
        if not p.exists():
            safe_print(f"[NOT_FOUND] {arg}")
            total_hits += 1
            continue

        hits = check_file(p)
        for line_no, content in hits:
            total_hits += 1
            if line_no > 0:
                safe_print(f"[MOJIBAKE] {p.as_posix()}:{line_no}")
                safe_print(content)
            else:
                safe_print(f"[MOJIBAKE] {p.as_posix()}")
                safe_print(content)

    if total_hits == 0:
        safe_print("OK: no mojibake detected")
        return 0
    return 1


if __name__ == "__main__":
    raise SystemExit(main())