const { clearSession } = require("../_auth");

module.exports = function handler(req, res) {
  if (req.method !== "POST") return res.status(405).json({ ok: false, error: "Method not allowed" });
  clearSession(res);
  res.setHeader("Cache-Control", "no-store");
  return res.status(200).json({ ok: true });
};
