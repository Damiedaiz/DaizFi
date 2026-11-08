# DaizFi.Space

A lightweight, mobile-first music discovery experience. The public website lets visitors explore music categories and search YouTube. A private owner gateway unlocks the personal YouTube and Audiomack embed controls.

## Public and owner modes
- Public visitors can open the site, browse discovery categories and search YouTube.
- Only a visitor with the private gateway phrase can unlock owner playback controls.
- The gateway phrase is checked by a Vercel serverless function; it is not stored in browser code.
- Successful login creates a signed, HTTP-only, Secure, SameSite=Strict cookie.
- Owner sessions expire after seven days; use **Owner mode · Sign out** to end a session.
- This is a single-owner gateway, not a multi-user account or admin dashboard.

## Environment variables (Vercel)
Set both variables in Project → Settings → Environment Variables, for Production (and Preview/Development only if needed):
- `DAIZFI_GATEWAY_PHRASE`: your private login phrase. Use a long, unique phrase and do not paste it into GitHub or public chat.
- `DAIZFI_SESSION_SECRET`: a cryptographically random secret of at least 32 characters. Generate with `openssl rand -hex 32`.

Redeploy after adding or changing environment variables. Without both values configured, the gateway will not unlock.

## Paparazzi / audience insights
The Vercel Web Analytics script is included in `index.html`. Enable Web Analytics in the Vercel project dashboard → Analytics, then redeploy. After owner sign-in, the **Paparazzi · Audience pulse** shortcut opens the Vercel dashboard.

Analytics can show aggregate visits, page views, referrers, countries, devices and browsers. It does not identify individual visitors or reveal WhatsApp Status viewers. It measures traffic that actually visits DaizFi.Space.

## Music embeds
- YouTube playback uses the official embedded player. YouTube controls playback availability and ads.
- Audiomack links are converted to the official embedded player URL. Audiomack controls availability and playback.
- The site does not download, extract, proxy or separately host music audio.

## Deploy
This is a static front end with Vercel serverless API functions under `api/`.
- Vercel: import this repository; use Framework Preset **Other**, leave Build Command empty, and set Output Directory to `.`.
- Ensure the `api/` directory is included in the deployment so the owner gateway endpoints are available.
