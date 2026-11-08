# DaizFi.Space

A lightweight, mobile-first music discovery MVP. The interface lets people explore music categories, search YouTube, and play a selected YouTube video inside the official embedded player.

## MVP features
- Responsive dark interface with DaizFi.Space branding
- Genre discovery shortcuts
- YouTube search handoff
- Paste a YouTube URL or 11-character video ID to play
- Embedded YouTube player; playback, availability and ads remain controlled by YouTube
- No audio downloading, extraction, proxying or separate audio hosting

## Run locally
Open `index.html` in a browser, or serve the repository root with any static web server.

## Deploy
This is a static site: no build command and no environment variables are required.
- Vercel: import this repository; set Framework Preset to **Other**, Build Command empty, Output Directory `.`.
- GitHub Pages: Settings → Pages → Deploy from a branch → `main` → `/ (root)`.

## Important platform note
This prototype uses YouTube embeds and links to YouTube search. It does not guarantee that every video can be embedded or that ads will appear on every playback. Follow YouTube API Services Terms and Developer Policies if adding YouTube Data API features later.
