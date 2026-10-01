import React from 'react';

/**
 * SkeletonLoader: Glass shimmer skeleton placeholder for loading states.
 * Renders pulsing translucent rectangles with animated gradient sweep.
 */
export default function SkeletonLoader({ variant = 'card', count = 1 }) {
  const shimmerStyle = {
    background: 'linear-gradient(135deg, rgba(255,255,255,0.65) 0%, rgba(255,255,255,0.35) 40%, rgba(255,255,255,0.65) 100%)',
    backdropFilter: 'blur(20px) saturate(180%)',
    WebkitBackdropFilter: 'blur(20px) saturate(180%)',
    borderRadius: '20px',
    border: '1px solid rgba(255,255,255,0.85)',
    position: 'relative',
    overflow: 'hidden',
  };

  const shimmerOverlay = {
    position: 'absolute',
    inset: 0,
    background: 'linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.6) 50%, transparent 100%)',
    animation: 'shimmerSweep 2s infinite ease-in-out',
    pointerEvents: 'none',
    borderRadius: 'inherit',
  };

  if (variant === 'kpi') {
    return (
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px', marginBottom: '28px' }}>
        {Array.from({ length: count }).map((_, i) => (
          <div key={i} style={{ ...shimmerStyle, padding: '22px 24px', height: '100px' }} className="skeleton-shimmer">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', height: '100%' }}>
              <div>
                <div style={{ width: '90px', height: '12px', borderRadius: '6px', background: 'rgba(148,163,184,0.2)', marginBottom: '12px' }} />
                <div style={{ width: '120px', height: '28px', borderRadius: '8px', background: 'rgba(148,163,184,0.15)' }} />
              </div>
              <div style={{ width: '52px', height: '52px', borderRadius: '16px', background: 'rgba(148,163,184,0.12)' }} />
            </div>
            <div style={shimmerOverlay} />
          </div>
        ))}
      </div>
    );
  }

  if (variant === 'table') {
    return (
      <div style={{ ...shimmerStyle, padding: '0', borderRadius: '16px' }} className="skeleton-shimmer">
        {/* Header row */}
        <div style={{ display: 'flex', gap: '20px', padding: '14px 18px', background: 'rgba(248,250,252,0.85)', borderBottom: '1px solid rgba(226,232,240,0.6)' }}>
          {[80, 140, 100, 120, 90].map((w, i) => (
            <div key={i} style={{ width: `${w}px`, height: '14px', borderRadius: '7px', background: 'rgba(148,163,184,0.2)' }} />
          ))}
        </div>
        {/* Body rows */}
        {Array.from({ length: count }).map((_, i) => (
          <div key={i} style={{ display: 'flex', gap: '20px', padding: '15px 18px', borderBottom: '1px solid rgba(226,232,240,0.3)' }}>
            {[80, 140, 100, 120, 90].map((w, j) => (
              <div key={j} style={{ width: `${w}px`, height: '14px', borderRadius: '7px', background: `rgba(148,163,184,${0.1 + (j * 0.02)})` }} />
            ))}
          </div>
        ))}
        <div style={shimmerOverlay} />
      </div>
    );
  }

  // Default card skeleton
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} style={{ ...shimmerStyle, padding: '26px', minHeight: '160px' }} className="skeleton-shimmer">
          <div style={{ width: '40%', height: '18px', borderRadius: '9px', background: 'rgba(148,163,184,0.2)', marginBottom: '16px' }} />
          <div style={{ width: '90%', height: '12px', borderRadius: '6px', background: 'rgba(148,163,184,0.12)', marginBottom: '10px' }} />
          <div style={{ width: '70%', height: '12px', borderRadius: '6px', background: 'rgba(148,163,184,0.1)', marginBottom: '10px' }} />
          <div style={{ width: '55%', height: '12px', borderRadius: '6px', background: 'rgba(148,163,184,0.08)' }} />
          <div style={shimmerOverlay} />
        </div>
      ))}
    </div>
  );
}
