const { safeEqual, setSession } = require("../_auth");

module.exports = function handler(req, res) {
  if (req.method !== "POST") return res.status(405).json({ ok: false, error: "Method not allowed" });
  const expected = process.env.DAIZFI_GATEWAY_PHRASE;
  if (!expected) return res.status(503).json({ ok: false, error: "Owner gateway is not configured yet." });
  const supplied = req.body && typeof req.body.phrase === "string" ? req.body.phrase : "";
  if (!safeEqual(supplied, expected)) return res.status(401).json({ ok: false, error: "Access phrase not recognised." });
  setSession(res);
  res.setHeader("Cache-Control", "no-store");
  return res.status(200).json({ ok: true });
};
