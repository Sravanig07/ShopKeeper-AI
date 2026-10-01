import React from 'react';

/**
 * AmbientGlow: Apple Liquid Glass Dynamic Aurora Mesh
 * Organic morphing fluid caustics with Apple Intelligence color harmonies.
 */
export default function AmbientGlow() {
  return (
    <div
      className="ambient-glow-container"
      style={{
        position: 'fixed',
        inset: 0,
        pointerEvents: 'none',
        overflow: 'hidden',
        zIndex: 0,
      }}
    >
      {/* Liquid Fluid 1: Apple Aurora (Violet / Indigo / Electric Fuchsia) */}
      <div
        className="liquid-blob blob-aurora"
        style={{
          position: 'absolute',
          top: '-8%',
          right: '8%',
          width: '650px',
          height: '650px',
          background: 'linear-gradient(135deg, rgba(168, 85, 247, 0.22) 0%, rgba(99, 102, 241, 0.18) 50%, rgba(244, 63, 94, 0.12) 100%)',
          filter: 'blur(75px)',
          animation: 'liquidBlobMorph1 24s infinite alternate ease-in-out',
        }}
      />

      {/* Liquid Fluid 2: Apple Aquatic (Cyan / Sky / Aquamarine) */}
      <div
        className="liquid-blob blob-aquatic"
        style={{
          position: 'absolute',
          bottom: '2%',
          left: '5%',
          width: '600px',
          height: '600px',
          background: 'linear-gradient(135deg, rgba(6, 182, 212, 0.20) 0%, rgba(56, 189, 248, 0.16) 50%, rgba(99, 102, 241, 0.12) 100%)',
          filter: 'blur(80px)',
          animation: 'liquidBlobMorph2 28s infinite alternate ease-in-out',
        }}
      />

      {/* Liquid Fluid 3: Apple Sunset Coral / Peach */}
      <div
        className="liquid-blob blob-sunset"
        style={{
          position: 'absolute',
          top: '35%',
          left: '38%',
          width: '520px',
          height: '520px',
          background: 'linear-gradient(135deg, rgba(251, 113, 133, 0.16) 0%, rgba(244, 114, 182, 0.12) 50%, rgba(251, 191, 36, 0.08) 100%)',
          filter: 'blur(85px)',
          animation: 'liquidBlobMorph3 22s infinite alternate ease-in-out',
        }}
      />

      {/* Liquid Fluid 4: Emerald / Spring Mint Subtle Luminous Core */}
      <div
        className="liquid-blob blob-mint"
        style={{
          position: 'absolute',
          top: '65%',
          right: '18%',
          width: '450px',
          height: '450px',
          background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.14) 0%, rgba(45, 212, 191, 0.10) 100%)',
          filter: 'blur(90px)',
          animation: 'liquidBlobMorph4 26s infinite alternate ease-in-out',
        }}
      />

      {/* VisionOS Specular Liquid Caustic Overlay */}
      <div
        className="liquid-caustic-overlay"
        style={{
          position: 'absolute',
          inset: 0,
          background:
            'radial-gradient(ellipse at 50% 20%, rgba(255, 255, 255, 0.35) 0%, transparent 60%), radial-gradient(ellipse at 80% 80%, rgba(255, 255, 255, 0.2) 0%, transparent 50%)',
          mixBlendMode: 'overlay',
          opacity: 0.65,
        }}
      />

      {/* Apple Micro-Dot Spatial Grid */}
      <div
        className="spatial-grid-overlay"
        style={{
          position: 'absolute',
          inset: 0,
          backgroundImage:
            'radial-gradient(rgba(99, 102, 241, 0.08) 1px, transparent 1px)',
          backgroundSize: '28px 28px',
          maskImage: 'radial-gradient(ellipse at 50% 50%, black 50%, transparent 90%)',
          WebkitMaskImage: 'radial-gradient(ellipse at 50% 50%, black 50%, transparent 90%)',
        }}
      />
    </div>
  );
}
