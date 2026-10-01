import React, { useEffect, useRef, useCallback } from 'react';

/**
 * ParticleExplosion: Canvas-based confetti burst effect.
 * Spawns colorful particles with physics (gravity, rotation, fade).
 * Triggered via `trigger` prop toggle.
 */
export default function ParticleExplosion({ trigger, duration = 2500, particleCount = 80 }) {
  const canvasRef = useRef(null);
  const animRef = useRef(null);
  const prevTrigger = useRef(trigger);

  const brandColors = [
    '#6366f1', '#a855f7', '#06b6d4', '#10b981', '#f43f5e',
    '#fbbf24', '#4f46e5', '#c084fc', '#38bdf8', '#34d399',
    '#f472b6', '#fb923c',
  ];

  const explode = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    const particles = [];
    const centerX = canvas.width / 2;
    const centerY = canvas.height * 0.35;

    for (let i = 0; i < particleCount; i++) {
      const angle = (Math.PI * 2 * i) / particleCount + (Math.random() - 0.5) * 0.5;
      const velocity = 4 + Math.random() * 10;
      const size = 4 + Math.random() * 8;
      const isRound = Math.random() > 0.4;
      
      particles.push({
        x: centerX,
        y: centerY,
        vx: Math.cos(angle) * velocity,
        vy: Math.sin(angle) * velocity - 3,
        size,
        color: brandColors[Math.floor(Math.random() * brandColors.length)],
        rotation: Math.random() * 360,
        rotationSpeed: (Math.random() - 0.5) * 12,
        opacity: 1,
        gravity: 0.12 + Math.random() * 0.08,
        drag: 0.985 + Math.random() * 0.01,
        isRound,
        width: isRound ? size : size * (0.3 + Math.random() * 0.5),
        height: isRound ? size : size * (1 + Math.random() * 1.5),
      });
    }

    const startTime = performance.now();

    const animate = (timestamp) => {
      const elapsed = timestamp - startTime;
      if (elapsed > duration) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        return;
      }

      ctx.clearRect(0, 0, canvas.width, canvas.height);
      const fadeStart = duration * 0.6;

      for (const p of particles) {
        p.vx *= p.drag;
        p.vy *= p.drag;
        p.vy += p.gravity;
        p.x += p.vx;
        p.y += p.vy;
        p.rotation += p.rotationSpeed;

        if (elapsed > fadeStart) {
          p.opacity = Math.max(0, 1 - (elapsed - fadeStart) / (duration - fadeStart));
        }

        ctx.save();
        ctx.translate(p.x, p.y);
        ctx.rotate((p.rotation * Math.PI) / 180);
        ctx.globalAlpha = p.opacity;
        ctx.fillStyle = p.color;
        ctx.shadowBlur = 6;
        ctx.shadowColor = p.color;

        if (p.isRound) {
          ctx.beginPath();
          ctx.arc(0, 0, p.size / 2, 0, Math.PI * 2);
          ctx.fill();
        } else {
          ctx.fillRect(-p.width / 2, -p.height / 2, p.width, p.height);
        }

        ctx.restore();
      }

      animRef.current = requestAnimationFrame(animate);
    };

    animRef.current = requestAnimationFrame(animate);
  }, [particleCount, duration]);

  useEffect(() => {
    if (trigger && trigger !== prevTrigger.current) {
      explode();
    }
    prevTrigger.current = trigger;

    return () => {
      if (animRef.current) cancelAnimationFrame(animRef.current);
    };
  }, [trigger, explode]);

  return (
    <canvas
      ref={canvasRef}
      style={{
        position: 'fixed',
        inset: 0,
        pointerEvents: 'none',
        zIndex: 9999,
      }}
    />
  );
}
