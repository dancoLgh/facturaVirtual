const canvas = document.getElementById('game');
const ctx = canvas.getContext('2d');

const ui = {
  level: document.getElementById('level'),
  score: document.getElementById('score'),
  weaponLevel: document.getElementById('weaponLevel'),
  lives: document.getElementById('lives'),
  restart: document.getElementById('restart')
};

const W = canvas.width;
const H = canvas.height;
const LANES = [W * 0.22, W * 0.5, W * 0.78];

const input = { left: false, right: false };

let state;

function resetGame() {
  state = {
    over: false,
    level: 1,
    score: 0,
    lives: 3,
    weaponLevel: 1,
    soldierCount: 1,
    fireRate: 0.35,
    bulletDamage: 1,
    enemySpeed: 45,
    spawnEvery: 1.2,
    dist: 0,
    gates: [],
    enemies: [],
    bullets: [],
    particles: [],
    player: {
      lane: 1,
      x: LANES[1],
      y: H - 80,
      cooldown: 0
    }
  };

  ui.restart.hidden = true;
  syncHud();
}

function syncHud() {
  ui.level.textContent = state.level;
  ui.score.textContent = Math.floor(state.score);
  ui.weaponLevel.textContent = `${state.weaponLevel} (x${state.soldierCount})`;
  ui.lives.textContent = state.lives;
}

function spawnGate() {
  const lane = Math.floor(Math.random() * 3);
  const roll = Math.random();
  let gate;

  if (roll < 0.2) {
    gate = { type: 'danger', value: -Math.ceil(5 + state.level * 1.5), lane, y: -20 };
  } else if (roll < 0.45) {
    gate = { type: 'soldiers', value: Math.ceil(1 + state.level / 3), lane, y: -20 };
  } else if (roll < 0.7) {
    gate = { type: 'damage', value: 1, lane, y: -20 };
  } else if (roll < 0.85) {
    gate = { type: 'firerate', value: 0.03, lane, y: -20 };
  } else {
    gate = { type: 'points', value: 150 + state.level * 20, lane, y: -20 };
  }

  state.gates.push(gate);
}

function spawnEnemy() {
  const lane = Math.floor(Math.random() * 3);
  const hpBase = 8 + state.level * 2;
  state.enemies.push({
    lane,
    x: LANES[lane],
    y: -40,
    hp: hpBase,
    maxHp: hpBase,
    speed: state.enemySpeed + Math.random() * 20
  });
}

function shoot(dt) {
  const p = state.player;
  p.cooldown -= dt;
  if (p.cooldown > 0) return;

  p.cooldown = Math.max(0.08, state.fireRate);
  for (let i = 0; i < state.soldierCount; i += 1) {
    const spread = (i - (state.soldierCount - 1) / 2) * 12;
    state.bullets.push({
      x: p.x + spread,
      y: p.y - 20,
      v: 360,
      damage: state.bulletDamage
    });
  }
}

function applyGate(g) {
  if (g.type === 'danger') {
    state.lives += Math.floor(g.value / 8);
    state.score += g.value * 5;
  }
  if (g.type === 'soldiers') state.soldierCount = Math.min(8, state.soldierCount + g.value);
  if (g.type === 'damage') {
    state.bulletDamage += g.value;
    state.weaponLevel += 1;
  }
  if (g.type === 'firerate') {
    state.fireRate = Math.max(0.12, state.fireRate - g.value);
    state.weaponLevel += 1;
  }
  if (g.type === 'points') state.score += g.value;

  if (state.lives <= 0) {
    state.over = true;
    ui.restart.hidden = false;
  }

  syncHud();
}

function levelUpIfNeeded() {
  const nextLevelAt = state.level * 1300;
  if (state.score < nextLevelAt) return;

  state.level += 1;
  state.enemySpeed += 10;
  state.spawnEvery = Math.max(0.45, state.spawnEvery - 0.08);
  state.fireRate = Math.max(0.1, state.fireRate - 0.01);
  state.score += 120;
  syncHud();
}

function hitFx(x, y, color = '#ffd36d') {
  for (let i = 0; i < 8; i += 1) {
    state.particles.push({
      x,
      y,
      vx: (Math.random() - 0.5) * 90,
      vy: (Math.random() - 0.5) * 90,
      t: 0.4,
      color
    });
  }
}

function update(dt) {
  if (state.over) return;

  if (input.left) state.player.lane = Math.max(0, state.player.lane - dt * 9);
  if (input.right) state.player.lane = Math.min(2, state.player.lane + dt * 9);
  const laneRounded = Math.round(state.player.lane);
  state.player.x += (LANES[laneRounded] - state.player.x) * Math.min(1, dt * 12);

  state.dist += dt;
  if (Math.random() < dt / state.spawnEvery) spawnGate();
  if (Math.random() < dt * (0.9 + state.level * 0.12)) spawnEnemy();

  shoot(dt);

  for (const gate of state.gates) {
    gate.y += 140 * dt;
    if (Math.abs(gate.y - state.player.y) < 35 && Math.abs(LANES[gate.lane] - state.player.x) < 40) {
      applyGate(gate);
      gate.y = H + 60;
    }
  }

  for (const b of state.bullets) b.y -= b.v * dt;

  for (const e of state.enemies) {
    e.y += e.speed * dt;

    for (const b of state.bullets) {
      if (Math.abs(b.x - e.x) < 18 && Math.abs(b.y - e.y) < 20) {
        e.hp -= b.damage;
        b.y = -100;
        hitFx(e.x, e.y, '#ff9a70');
      }
    }

    if (e.hp <= 0) {
      e.y = H + 100;
      state.score += 65 + state.level * 8;
      hitFx(e.x, e.y, '#7bff9f');
    }

    if (e.y > H - 65) {
      e.y = H + 100;
      state.lives -= 1;
      hitFx(e.x, H - 50, '#ff3e3e');
      if (state.lives <= 0) {
        state.over = true;
        ui.restart.hidden = false;
      }
    }
  }

  for (const p of state.particles) {
    p.x += p.vx * dt;
    p.y += p.vy * dt;
    p.t -= dt;
  }

  state.gates = state.gates.filter((g) => g.y < H + 50);
  state.enemies = state.enemies.filter((e) => e.y < H + 80);
  state.bullets = state.bullets.filter((b) => b.y > -40);
  state.particles = state.particles.filter((p) => p.t > 0);

  levelUpIfNeeded();
  syncHud();
}

function drawBridge() {
  ctx.fillStyle = '#7d8fa5';
  ctx.fillRect(W * 0.18, 0, W * 0.64, H);

  ctx.strokeStyle = '#4f6279';
  ctx.lineWidth = 4;
  for (let y = 0; y < H; y += 38) {
    ctx.beginPath();
    ctx.moveTo(W * 0.18, y);
    ctx.lineTo(W * 0.82, y + 20);
    ctx.stroke();
  }
}

function drawPlayer() {
  ctx.save();
  ctx.translate(state.player.x, state.player.y);

  for (let i = 0; i < state.soldierCount; i += 1) {
    const spread = (i - (state.soldierCount - 1) / 2) * 13;
    ctx.fillStyle = '#2f67ff';
    ctx.fillRect(spread - 7, -12, 14, 22);
    ctx.fillStyle = '#d4af56';
    ctx.fillRect(spread - 8, -18, 16, 8);
  }

  ctx.restore();
}

function drawEnemies() {
  for (const e of state.enemies) {
    ctx.fillStyle = '#b0c0d0';
    ctx.fillRect(e.x - 12, e.y - 16, 24, 24);

    const hpw = 30;
    ctx.fillStyle = '#2a2a2a';
    ctx.fillRect(e.x - hpw / 2, e.y - 24, hpw, 4);
    ctx.fillStyle = '#ff4a4a';
    ctx.fillRect(e.x - hpw / 2, e.y - 24, (hpw * e.hp) / e.maxHp, 4);
  }
}

function drawGates() {
  const labels = {
    danger: ['🔴', '#d73a3a'],
    soldiers: ['🟢', '#27b24e'],
    damage: ['🔵', '#3678e6'],
    firerate: ['🟣', '#8541d8'],
    points: ['🟠', '#da8a2f']
  };

  for (const g of state.gates) {
    const [icon, color] = labels[g.type];
    const x = LANES[g.lane];

    ctx.fillStyle = color;
    ctx.fillRect(x - 50, g.y - 16, 100, 34);
    ctx.fillStyle = '#fff';
    ctx.font = 'bold 16px sans-serif';
    const valueTxt = g.value > 0 ? `+${g.value}` : `${g.value}`;
    ctx.fillText(`${icon} ${valueTxt}`, x - 36, g.y + 6);
  }
}

function drawBullets() {
  ctx.strokeStyle = '#ffd978';
  ctx.lineWidth = 3;
  for (const b of state.bullets) {
    ctx.beginPath();
    ctx.moveTo(b.x, b.y);
    ctx.lineTo(b.x, b.y + 12);
    ctx.stroke();
  }
}

function drawParticles() {
  for (const p of state.particles) {
    ctx.globalAlpha = Math.max(0, p.t * 2);
    ctx.fillStyle = p.color;
    ctx.fillRect(p.x, p.y, 3, 3);
  }
  ctx.globalAlpha = 1;
}

function draw() {
  ctx.clearRect(0, 0, W, H);
  drawBridge();
  drawGates();
  drawEnemies();
  drawBullets();
  drawParticles();
  drawPlayer();

  if (state.over) {
    ctx.fillStyle = 'rgba(0,0,0,0.65)';
    ctx.fillRect(0, 0, W, H);
    ctx.fillStyle = '#fff';
    ctx.textAlign = 'center';
    ctx.font = 'bold 38px sans-serif';
    ctx.fillText('GAME OVER', W / 2, H / 2 - 20);
    ctx.font = '22px sans-serif';
    ctx.fillText(`Puntaje: ${Math.floor(state.score)}`, W / 2, H / 2 + 20);
  }
}

let last = performance.now();
function loop(now) {
  const dt = Math.min(0.033, (now - last) / 1000);
  last = now;
  update(dt);
  draw();
  requestAnimationFrame(loop);
}

window.addEventListener('keydown', (e) => {
  if (e.key === 'a' || e.key === 'ArrowLeft') input.left = true;
  if (e.key === 'd' || e.key === 'ArrowRight') input.right = true;
});

window.addEventListener('keyup', (e) => {
  if (e.key === 'a' || e.key === 'ArrowLeft') input.left = false;
  if (e.key === 'd' || e.key === 'ArrowRight') input.right = false;
});

ui.restart.addEventListener('click', resetGame);

resetGame();
requestAnimationFrame(loop);
