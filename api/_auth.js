const crypto = require("crypto");

const COOKIE_NAME = "daizfi_owner";
const MAX_AGE = 60 * 60 * 24 * 7;

function secret() {
  const value = process.env.DAIZFI_SESSION_SECRET;
  if (!value || value.length < 32) throw new Error("DAIZFI_SESSION_SECRET must be at least 32 characters.");
  return value;
}
function sign(value) {
  return crypto.createHmac("sha256", secret()).update(value).digest("base64url");
}
function safeEqual(a, b) {
  const left = Buffer.from(String(a));
  const right = Buffer.from(String(b));
  return left.length === right.length && crypto.timingSafeEqual(left, right);
}
function cookieValue(req) {
  const raw = req.headers.cookie || "";
  const item = raw.split(";").map(v => v.trim()).find(v => v.startsWith(COOKIE_NAME + "="));
  return item ? decodeURIComponent(item.slice(COOKIE_NAME.length + 1)) : "";
}
function validSession(req) {
  try {
    const token = cookieValue(req);
    const [payload, signature] = token.split(".");
    if (!payload || !signature || !safeEqual(sign(payload), signature)) return false;
    const data = JSON.parse(Buffer.from(payload, "base64url").toString("utf8"));
    return data.role === "owner" && Number(data.exp) > Date.now();
  } catch { return false; }
}
function setSession(res) {
  const payload = Buffer.from(JSON.stringify({ role: "owner", exp: Date.now() + MAX_AGE * 1000 })).toString("base64url");
  const token = payload + "." + sign(payload);
  res.setHeader("Set-Cookie", COOKIE_NAME + "=" + encodeURIComponent(token) + "; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=" + MAX_AGE);
}
function clearSession(res) {
  res.setHeader("Set-Cookie", COOKIE_NAME + "=; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=0");
}
module.exports = { safeEqual, validSession, setSession, clearSession };
