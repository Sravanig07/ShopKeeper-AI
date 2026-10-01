import React from 'react';

/**
 * Floating3DCube: Pure CSS 3D isometric rotating cube with frosted glass faces,
 * specular edge illumination, and ambient iridescent glow.
 */
export default function Floating3DCube({ size = 70, className = '' }) {
  const half = size / 2;

  return (
    <div
      className={`floating-3d-scene ${className}`}
      style={{
        width: `${size}px`,
        height: `${size}px`,
        perspective: '800px',
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <div
        className="floating-3d-cube"
        style={{
          width: `${size}px`,
          height: `${size}px`,
          position: 'relative',
          transformStyle: 'preserve-3d',
          animation: 'rotateCube3D 14s infinite linear',
        }}
      >
        {/* Front Face */}
        <div
          className="cube-face"
          style={{
            position: 'absolute',
            width: `${size}px`,
            height: `${size}px`,
            transform: `rotateY(0deg) translateZ(${half}px)`,
            background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.75), rgba(168, 85, 247, 0.75))',
            backdropFilter: 'blur(8px)',
            border: '1.5px solid rgba(255, 255, 255, 0.65)',
            boxShadow: 'inset 0 0 15px rgba(255, 255, 255, 0.3), 0 0 20px rgba(99, 102, 241, 0.4)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: `${size * 0.32}px`,
            userSelect: 'none',
          }}
        >
          ₹
        </div>

        {/* Back Face */}
        <div
          className="cube-face"
          style={{
            position: 'absolute',
            width: `${size}px`,
            height: `${size}px`,
            transform: `rotateY(180deg) translateZ(${half}px)`,
            background: 'linear-gradient(135deg, rgba(236, 72, 153, 0.75), rgba(99, 102, 241, 0.75))',
            backdropFilter: 'blur(8px)',
            border: '1.5px solid rgba(255, 255, 255, 0.65)',
            boxShadow: 'inset 0 0 15px rgba(255, 255, 255, 0.3), 0 0 20px rgba(236, 72, 153, 0.4)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: `${size * 0.28}px`,
            userSelect: 'none',
          }}
        >
          AI
        </div>

        {/* Right Face */}
        <div
          className="cube-face"
          style={{
            position: 'absolute',
            width: `${size}px`,
            height: `${size}px`,
            transform: `rotateY(90deg) translateZ(${half}px)`,
            background: 'linear-gradient(135deg, rgba(14, 165, 233, 0.75), rgba(99, 102, 241, 0.75))',
            backdropFilter: 'blur(8px)',
            border: '1.5px solid rgba(255, 255, 255, 0.65)',
            boxShadow: 'inset 0 0 15px rgba(255, 255, 255, 0.3), 0 0 20px rgba(14, 165, 233, 0.4)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: `${size * 0.28}px`,
            userSelect: 'none',
          }}
        >
          POS
        </div>

        {/* Left Face */}
        <div
          className="cube-face"
          style={{
            position: 'absolute',
            width: `${size}px`,
            height: `${size}px`,
            transform: `rotateY(-90deg) translateZ(${half}px)`,
            background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.75), rgba(14, 165, 233, 0.75))',
            backdropFilter: 'blur(8px)',
            border: '1.5px solid rgba(255, 255, 255, 0.65)',
            boxShadow: 'inset 0 0 15px rgba(255, 255, 255, 0.3), 0 0 20px rgba(16, 185, 129, 0.4)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: `${size * 0.3}px`,
            userSelect: 'none',
          }}
        >
          📦
        </div>

        {/* Top Face */}
        <div
          className="cube-face"
          style={{
            position: 'absolute',
            width: `${size}px`,
            height: `${size}px`,
            transform: `rotateX(90deg) translateZ(${half}px)`,
            background: 'linear-gradient(135deg, rgba(245, 158, 11, 0.8), rgba(236, 72, 153, 0.8))',
            backdropFilter: 'blur(8px)',
            border: '1.5px solid rgba(255, 255, 255, 0.7)',
            boxShadow: 'inset 0 0 15px rgba(255, 255, 255, 0.4), 0 0 20px rgba(245, 158, 11, 0.4)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: `${size * 0.3}px`,
            userSelect: 'none',
          }}
        >
          ⚡
        </div>

        {/* Bottom Face */}
        <div
          className="cube-face"
          style={{
            position: 'absolute',
            width: `${size}px`,
            height: `${size}px`,
            transform: `rotateX(-90deg) translateZ(${half}px)`,
            background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.85), rgba(79, 70, 229, 0.85))',
            backdropFilter: 'blur(8px)',
            border: '1.5px solid rgba(255, 255, 255, 0.65)',
            boxShadow: 'inset 0 0 15px rgba(255, 255, 255, 0.3), 0 0 25px rgba(99, 102, 241, 0.5)',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontWeight: 800,
            fontSize: `${size * 0.28}px`,
            userSelect: 'none',
          }}
        >
          IQ
        </div>
      </div>
    </div>
  );
}
