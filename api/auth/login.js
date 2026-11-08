const { safeEqual, setSession } = require("../_auth");

module.exports = function handler(req, res) {
  res.setHeader("Content-Type", "application/json; charset=utf-8");
  res.setHeader("Cache-Control", "no-store");

  if (req.method !== "POST") {
    return res.status(405).json({ ok: false, error: "Method not allowed" });
  }

  try {
    const expected = process.env.DAIZFI_GATEWAY_PHRASE;
    if (!expected) {
      return res.status(503).json({
        ok: false,
        error: "Owner gateway is not configured yet. Add DAIZFI_GATEWAY_PHRASE in Vercel."
      });
    }

    const supplied = req.body && typeof req.body.phrase === "string" ? req.body.phrase : "";

    if (!safeEqual(supplied, expected)) {
      return res.status(401).json({ ok: false, error: "Access phrase not recognised." });
    }

    setSession(res);

    return res.status(200).json({ ok: true });
  } catch (error) {
    console.error("DaizFi gateway login error:", error);
    return res.status(500).json({
      ok: false,
      error: "Owner gateway server configuration error. Check DAIZFI_SESSION_SECRET in Vercel."
    });
  }
};
