#!/usr/bin/env python3
"""Genera la web de la documentación a partir de los .md de docs/.

Estructura esperada (una carpeta por boletín; si hay varias memorias del mismo
boletín, una subcarpeta por persona):

    docs/boletin1/maria/boletin1.md
    docs/boletin1/lucia/boletin1.md      (+ sus imágenes al lado)
    docs/boletin2/boletin2.md            (+ docs/boletin2/capturas/...)

Uso (desde la raíz del repositorio):

    pip install -r docs/requirements.txt
    python docs/build_site.py

Cada .md genera un .html en la misma ruta dentro de docs/site/, de modo que las
rutas relativas de las imágenes siguen funcionando, y se crea un index.html con
todos los boletines. Todas las imágenes de docs/ se copian a docs/site/.
"""

import html
import os
import re
import shutil
import sys
from pathlib import Path
from urllib.parse import unquote

from markdown_it import MarkdownIt

DOCS = Path(__file__).resolve().parent
SITE = DOCS / "site"
IMAGENES = {".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp"}
TITULO_WEB = "Prácticas Continuas"
SUBTITULO = "Boletines grupo 7 · API de tareas"
# Nombre que se muestra para cada carpeta de persona (si no está, se usa la carpeta).
# El orden de este diccionario es el orden en que se muestran las versiones.
AUTORES = {
    "maria": "Maria",
    "maria_lucia": "Maria y Lucía",
    "lucia": "Lucía",
    "nieves": "Nieves",
}

CSS = """
:root{--bg:#fff;--fg:#1f2328;--muted:#656d76;--line:#d0d7de;--card:#f6f8fa;--accent:#0969da;--code:#eff1f3}
@media (prefers-color-scheme:dark){:root{--bg:#0d1117;--fg:#e6edf3;--muted:#8d96a0;--line:#30363d;--card:#161b22;--accent:#58a6ff;--code:#1f2630}}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--fg);font:16px/1.6 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif}
a{color:var(--accent)}
header.top{border-bottom:1px solid var(--line);background:var(--card)}
header.top .in{max-width:1200px;margin:0 auto;padding:12px 16px;display:flex;gap:16px;align-items:baseline;flex-wrap:wrap}
header.top a.brand{font-weight:700;color:var(--fg);text-decoration:none}
header.top span{color:var(--muted);font-size:.9rem}
.layout{max-width:1200px;margin:0 auto;padding:24px 16px 64px;display:flex;gap:40px;align-items:flex-start}
main{min-width:0;flex:1;max-width:860px;overflow-wrap:anywhere}
pre,pre code{overflow-wrap:normal}
nav.toc{display:none;width:250px;flex:none;position:sticky;top:16px;max-height:calc(100vh - 32px);overflow:auto;font-size:.85rem}
nav.toc b{display:block;margin-bottom:8px;color:var(--muted);text-transform:uppercase;font-size:.75rem;letter-spacing:.04em}
nav.toc a{display:block;padding:2px 0;color:var(--fg);text-decoration:none}
nav.toc a:hover{color:var(--accent)}
nav.toc a.l3{padding-left:14px;color:var(--muted)}
@media (min-width:1100px){nav.toc{display:block}}
h1{font-size:2rem;margin:.2em 0 .6em;border-bottom:1px solid var(--line);padding-bottom:.3em}
h2{margin-top:2em;border-bottom:1px solid var(--line);padding-bottom:.25em}
h3{margin-top:1.6em}
img{max-width:100%;height:auto;border:1px solid var(--line);border-radius:6px;margin:.4em 0}
pre{background:var(--code);padding:12px 14px;border-radius:6px;overflow-x:auto;font-size:.85rem;line-height:1.45}
code{background:var(--code);padding:.15em .35em;border-radius:4px;font-size:.88em}
pre code{background:none;padding:0}
.table-wrap{overflow-x:auto;margin:1em 0}
table{border-collapse:collapse;width:100%;font-size:.9rem}
th,td{border:1px solid var(--line);padding:6px 10px;text-align:left;vertical-align:top}
th{background:var(--card)}
.table-wrap th,.table-wrap td{overflow-wrap:normal}
.table-wrap td a{white-space:nowrap}
blockquote{margin:1em 0;padding:.2em 1em;border-left:4px solid var(--line);color:var(--muted)}
.versiones{display:flex;gap:8px;flex-wrap:wrap;align-items:center;margin:0 0 1.2rem;font-size:.9rem;color:var(--muted)}
.versiones a,.versiones b{padding:3px 10px;border:1px solid var(--line);border-radius:999px;text-decoration:none}
.versiones b{background:var(--accent);border-color:var(--accent);color:#fff}
.fuente{margin-top:3rem;padding-top:1rem;border-top:1px solid var(--line);font-size:.85rem;color:var(--muted)}
.cards{display:grid;gap:14px;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));margin-top:1.5rem}
.card{position:relative;border:1px solid var(--line);border-radius:8px;padding:14px 16px;background:var(--card)}
.card:hover{border-color:var(--accent)}
.card h2{margin:0 0 6px;border:0;padding:0;font-size:1.15rem}
.card h2 a{color:var(--accent);text-decoration:none}
.card h2 a::after{content:"";position:absolute;inset:0}
.card ul{margin:0;padding-left:1.1em;color:var(--muted);font-size:.85rem}
.card ul.vers{list-style:none;padding:0;display:flex;flex-direction:column;gap:6px;position:relative}
.card ul.vers a{display:block;padding:6px 10px;border:1px solid var(--line);border-radius:6px;background:var(--bg);text-decoration:none;color:var(--fg);font-size:.9rem}
.card ul.vers a:hover{border-color:var(--accent);color:var(--accent)}
.lead{color:var(--muted);margin-top:-.4em}
"""

FRONT_MATTER = re.compile(r"\A---[ \t]*\n(.*?)\n---[ \t]*\n", re.S)


def quitar_front_matter(texto):
    """Quita la cabecera YAML (--- ... ---) y devuelve (texto, datos)."""
    m = FRONT_MATTER.match(texto)
    if not m:
        return texto, {}
    datos = {}
    for linea in m.group(1).splitlines():
        k, _, v = linea.partition(":")
        if v:
            datos[k.strip()] = v.strip().strip("\"'")
    return texto[m.end():], datos


def texto_plano(token):
    """Texto de un token 'inline' (sin formato), para títulos y anclas."""
    return "".join(
        t.content for t in (token.children or []) if t.type in ("text", "code_inline")
    )


def slug(texto, usados):
    """Ancla al estilo GitHub: minúsculas, sin signos, espacios -> guiones."""
    base = re.sub(r"[^\w\- ]", "", texto.lower()).strip().replace(" ", "-") or "seccion"
    nombre, n = base, 1
    while nombre in usados:
        n += 1
        nombre = f"{base}-{n}"
    usados.add(nombre)
    return nombre


def convertir(texto_md, nombre):
    """Markdown -> (html, título, índice de secciones, imágenes locales)."""
    texto_md, datos = quitar_front_matter(texto_md)
    md = MarkdownIt("commonmark", {"html": True}).enable("table").enable("strikethrough")
    tokens = md.parse(texto_md)
    usados, indice, imagenes, titulo = set(), [], [], datos.get("title") or nombre
    primero = "title" not in datos
    for i, tok in enumerate(tokens):
        if tok.type == "heading_open":
            inline = tokens[i + 1]
            nivel = int(tok.tag[1])
            tok.attrSet("id", slug(texto_plano(inline), usados))
            if nivel == 1 and primero:
                titulo, primero = texto_plano(inline), False
            elif nivel in (2, 3):
                indice.append((nivel, texto_plano(inline), tok.attrGet("id")))
        if tok.type == "inline":
            for hijo in tok.children or []:
                if hijo.type == "image":
                    src = hijo.attrGet("src") or ""
                    if not re.match(r"^(https?:)?//|^data:", src):
                        imagenes.append(unquote(src))
                elif hijo.type == "link_open":
                    href = hijo.attrGet("href") or ""
                    if re.match(r"^https?://", href):
                        hijo.attrSet("target", "_blank")
                        hijo.attrSet("rel", "noopener")
                    elif re.match(r"^[^:#]+\.md(#.*)?$", href):
                        hijo.attrSet("href", re.sub(r"\.md(#.*)?$", r".html\1", href))
    cuerpo = md.renderer.render(tokens, md.options, {})
    # Las capturas se pueden abrir a tamaño completo y las tablas hacen scroll.
    cuerpo = re.sub(
        r'<img src="([^"]+)"([^>]*?)\s*/?>',
        r'<a href="\1"><img src="\1"\2 loading="lazy"></a>',
        cuerpo,
    )
    cuerpo = cuerpo.replace("<table>", '<div class="table-wrap"><table>').replace(
        "</table>", "</table></div>"
    )
    return cuerpo, titulo, indice, imagenes


def pagina(titulo, cuerpo, raiz_rel, toc="", extra_antes="", extra_despues=""):
    """HTML completo. raiz_rel: ruta relativa desde la página hasta docs/site/."""
    toc_html = f'<nav class="toc"><b>En esta página</b>{toc}</nav>' if toc else ""
    return f"""<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{html.escape(titulo)} · {TITULO_WEB}</title>
<style>{CSS}</style>
</head>
<body>
<header class="top"><div class="in"><a class="brand" href="{raiz_rel}index.html">{TITULO_WEB}</a><span>{SUBTITULO}</span></div></header>
<div class="layout">
<main>
{extra_antes}
{cuerpo}
{extra_despues}
</main>
{toc_html}
</div>
</body>
</html>
"""


def numero(carpeta):
    """boletin2 -> 2 (para ordenar boletin2 antes que boletin10)."""
    return int(re.search(r"(\d+)", carpeta.name).group(1))


def etiqueta_version(md):
    """Nombre de la persona a partir de la subcarpeta; '' si el .md está suelto."""
    if md.parent.parent.name.startswith("boletin"):  # docs/boletinN/<persona>/x.md
        return AUTORES.get(md.parent.name, md.parent.name.replace("_", " ").title())
    return ""


def main():
    carpetas = sorted((c for c in DOCS.glob("boletin[0-9]*") if c.is_dir()), key=numero)
    orden = list(AUTORES)
    grupos = [
        (c, sorted(c.rglob("*.md"), key=lambda m: orden.index(m.parent.name) if m.parent.name in orden else 99))
        for c in carpetas
    ]
    grupos = [(c, mds) for c, mds in grupos if mds]
    if not grupos:
        sys.exit("No hay ningún docs/boletinN/**/*.md que convertir.")

    if SITE.exists():
        shutil.rmtree(SITE)
    SITE.mkdir()

    # Copia todas las imágenes de docs/ conservando la estructura de carpetas.
    for img in DOCS.rglob("*"):
        if img.is_file() and img.suffix.lower() in IMAGENES and SITE not in img.parents:
            destino = SITE / img.relative_to(DOCS)
            destino.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(img, destino)

    repo = os.environ.get("GITHUB_REPOSITORY")
    servidor = os.environ.get("GITHUB_SERVER_URL", "https://github.com")
    errores, tarjetas = [], ""
    for carpeta, mds in grupos:
        versiones = [(etiqueta_version(md), md) for md in mds]
        etiquetas = [e for e, _ in versiones]
        for etiqueta, md in versiones:
            rel = md.relative_to(DOCS)
            salida = SITE / rel.with_suffix(".html")
            raiz_rel = "../" * (len(rel.parts) - 1)
            cuerpo, titulo, indice, imagenes = convertir(md.read_text(encoding="utf-8"), md.stem)
            for img in imagenes:
                if not (salida.parent / img).is_file():
                    errores.append(f"{rel}: falta la imagen '{img}'")

            selector = ""
            if len(versiones) > 1:  # varias memorias del mismo boletín
                piezas = []
                for e, otro in versiones:
                    if otro == md:
                        piezas.append(f"<b>{html.escape(e)}</b>")
                    else:
                        ruta = os.path.relpath(SITE / otro.relative_to(DOCS).with_suffix(".html"), salida.parent)
                        piezas.append(f'<a href="{ruta.replace(os.sep, "/")}">{html.escape(e)}</a>')
                selector = f'<div class="versiones">Boletín de: {"".join(piezas)}</div>'
            fuente = ""
            if repo:  # solo en GitHub Actions: enlace al .md original
                fuente = (
                    f'<p class="fuente">Documento original: <a href="{servidor}/{repo}/blob/main/docs/'
                    f'{rel.as_posix()}">{html.escape(rel.as_posix())}</a></p>'
                )
            toc = "".join(f'<a class="l{n}" href="#{a}">{html.escape(t)}</a>' for n, t, a in indice)
            salida.parent.mkdir(parents=True, exist_ok=True)
            salida.write_text(pagina(titulo, cuerpo, raiz_rel, toc, selector, fuente), encoding="utf-8")
            print(f"  {rel} -> site/{rel.with_suffix('.html')} ({len(imagenes)} imágenes)")

            if len(versiones) == 1:
                partes = [t for n, t, _ in indice if t.startswith("Parte ")]
                lista = "".join(f"<li>{html.escape(p)}</li>" for p in partes)
                tarjetas += (
                    f'<div class="card"><h2><a href="{rel.with_suffix(".html").as_posix()}">'
                    f"Boletín {numero(carpeta)}</a></h2><ul>{lista}</ul></div>"
                )
        if len(versiones) > 1:
            enlaces = "".join(
                f'<li><a href="{md.relative_to(DOCS).with_suffix(".html").as_posix()}">'
                f"Boletín de {html.escape(e)}</a></li>"
                for e, md in versiones
            )
            tarjetas += (
                f'<div class="card"><h2>Boletín {numero(carpeta)}</h2>'
                f'<ul class="vers">{enlaces}</ul></div>'
            )

    portada = (
        f"<h1>{TITULO_WEB}</h1>"
        f'<p class="lead">{SUBTITULO}. Boletines de María Ballester Martínez, Lucía Olmos '
        "Martínez y María Nieves Turpín Gómez. </p>"
        f'<div class="cards">{tarjetas}</div>'
    )
    (SITE / "index.html").write_text(pagina("Índice", portada, ""), encoding="utf-8")
    print(f"  index.html ({len(grupos)} boletines)")

    if errores:
        print("\nERRORES:", *errores, sep="\n  ", file=sys.stderr)
        sys.exit(1)
    print(f"\nWeb generada en {SITE}")


if __name__ == "__main__":
    main()
