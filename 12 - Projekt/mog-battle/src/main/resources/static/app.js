const STATS = ["height", "jawline", "aura", "status", "frame"];
let chads = [];

// gleiche Formel wie MogEngine.mogScore, nur für die Anzeige
const mogScore = c => c.height * 2 + c.jawline * 2 + c.aura + c.status + c.frame;

const $ = id => document.getElementById(id);

async function loadChads() {
    chads = await (await fetch("/api/chads")).json();
    renderSelects();
    renderLeaderboard();
}

function renderSelects() {
    const keepA = $("chadA").value;
    const keepB = $("chadB").value;
    for (const select of [$("chadA"), $("chadB")]) {
        select.innerHTML = chads.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join("");
    }
    $("chadA").value = keepA || chads[0]?.id;
    $("chadB").value = keepB || chads[1]?.id;
    renderStats("A");
    renderStats("B");
}

function renderStats(side) {
    const chad = chads.find(c => String(c.id) === $("chad" + side).value);
    if (!chad) return;
    $("stats" + side).innerHTML = STATS.map(s => `
        <div class="stat">
            <span>${s}</span>
            <div class="bar"><div style="width:${chad[s] * 10}%"></div></div>
            <span>${chad[s]}</span>
        </div>`).join("") + `<div class="score">MogScore ${mogScore(chad)}</div>`;
}

function renderLeaderboard() {
    const sorted = [...chads].sort((a, b) => mogScore(b) - mogScore(a));
    $("leaderboard").innerHTML = sorted.map((c, i) => `
        <tr>
            <td>${i + 1}</td>
            <td>${escapeHtml(c.name)}</td>
            ${STATS.map(s => `<td class="num">${c[s]}</td>`).join("")}
            <td class="num"><b>${mogScore(c)}</b></td>
            <td><button class="small" data-upgrade="${c.id}">Upgrade?</button></td>
        </tr>`).join("");
}

async function battle() {
    const response = await fetch("/api/battles", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({chadAId: Number($("chadA").value), chadBId: Number($("chadB").value)})
    });
    const data = await response.json();
    $("result").classList.remove("hidden");
    const verdict = $("verdict");
    if (!response.ok) {
        verdict.className = "verdict";
        verdict.textContent = "NOPE";
        $("resultText").textContent = data.detail;
        return;
    }
    verdict.className = "verdict " + data.verdict;
    verdict.textContent = data.verdict.replace("_", " ");
    $("resultText").innerHTML = data.winner
        ? `<b>${escapeHtml(data.winner)}</b> mogt ${escapeHtml(data.loser)} mit ${data.margin} Punkten Vorsprung (inkl. Aura-Bonus).`
        : "Gleichstand. Niemand mogt hier niemanden.";
}

async function upgrade(id) {
    const response = await fetch(`/api/chads/${id}/upgrade`);
    const data = await response.json();
    $("upgradeText").textContent = response.ok
        ? `${data.current} (${data.currentScore}) würde zu ${data.upgrade} (${data.upgradeScore}) branch-swingen.`
        : data.detail;
}

async function createChad(event) {
    event.preventDefault();
    const form = new FormData(event.target);
    const body = {name: form.get("name")};
    STATS.forEach(s => body[s] = Number(form.get(s)));
    const response = await fetch("/api/chads", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        $("formError").textContent = "Ungültig: Name nötig, alle Stats zwischen 1 und 10.";
        return;
    }
    $("formError").textContent = "";
    event.target.reset();
    await loadChads();
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

$("chadA").addEventListener("change", () => renderStats("A"));
$("chadB").addEventListener("change", () => renderStats("B"));
$("battleBtn").addEventListener("click", battle);
$("createForm").addEventListener("submit", createChad);
$("leaderboard").addEventListener("click", e => {
    const id = e.target.dataset.upgrade;
    if (id) upgrade(id);
});

loadChads();
