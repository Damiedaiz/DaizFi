const { validSession } = require("../_auth");

module.exports = function handler(req, res) {
  res.setHeader("Cache-Control", "no-store");
  res.setHeader("Vary", "Cookie");
  return res.status(200).json({ authenticated: validSession(req) });
};
