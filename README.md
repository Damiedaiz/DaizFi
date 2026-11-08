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


## Private access (Vercel)
To make this owner-only, enable Vercel Authentication for **All Deployments** in Project → Security → Deployment Protection. On the current Vercel offering this can protect production on the Free plan; grant access only to your own Vercel account. A front-end PIN is intentionally not used because it would not secure the actual site.

## Vercel Web Analytics
The static Vercel Insights script is included in `index.html`. In the Vercel project dashboard, open **Analytics** and enable **Web Analytics**, then redeploy. Analytics can report visits, pages, referrers, countries, devices and browsers in aggregate; it cannot identify individual WhatsApp status viewers or reveal who viewed a WhatsApp status. It only measures people who click through to DaizFi.Space.

## Audiomack
The page accepts official Audiomack embed URLs copied from Audiomack's share/embed controls. It does not scrape Audiomack or extract audio. See [Audiomack's creator information](https://creators.audiomack.com/about) for its embeddable player details.
