"""
Script to sort the glossary entries.

Usage: python sort-glossary.py
"""

GLOSSARY_PATH = 'docs/89_glossary.md'

def main():
    with open(GLOSSARY_PATH, encoding='utf-8') as f:
        text = f.read()

    sep = '\n\n* **'
    parts = text.split(sep)
    # Normalize the amount of space between entries.
    parts = [part.strip() for part in parts]

    # Keep the intro at the beginning.
    first_part = parts[0]
    parts = sorted(parts[1:])

    parts = [first_part] + parts
    text = sep.join(parts)
    # End with a newline.
    text += '\n'

    with open(GLOSSARY_PATH, mode='w', encoding='utf-8') as f:
        f.write(text)


if __name__ == '__main__':
    main()
