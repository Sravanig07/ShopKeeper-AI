import React, { useState, useRef } from 'react';

/**
 * TiltCard: Interactive 3D Card with cursor perspective tilt,
 * specular reflection glare, and smooth spring-like recovery.
 */
export default function TiltCard({
  children,
  className = '',
  style = {},
  maxTilt = 12,
  glare = true,
  scale = 1.02,
  ...props
}) {
  const cardRef = useRef(null);
  const [transformStyle, setTransformStyle] = useState('');
  const [glareStyle, setGlareStyle] = useState({ opacity: 0 });
  const [isHovered, setIsHovered] = useState(false);

  const handleMouseMove = (e) => {
    if (!cardRef.current) return;
    const rect = cardRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    const rotateX = ((y - centerY) / centerY) * -maxTilt;
    const rotateY = ((x - centerX) / centerX) * maxTilt;

    setTransformStyle(
      `perspective(1000px) rotateX(${rotateX.toFixed(2)}deg) rotateY(${rotateY.toFixed(2)}deg) scale3d(${scale}, ${scale}, ${scale})`
    );

    if (glare) {
      const glareX = (x / rect.width) * 100;
      const glareY = (y / rect.height) * 100;
      setGlareStyle({
        opacity: 0.7,
        background: `radial-gradient(circle at ${glareX}% ${glareY}%, rgba(255, 255, 255, 0.85) 0%, rgba(192, 132, 252, 0.3) 22%, rgba(56, 189, 248, 0.22) 45%, transparent 70%)`,
      });
    }
  };

  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
    setTransformStyle('perspective(1000px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)');
    setGlareStyle({ opacity: 0 });
  };

  return (
    <div
      ref={cardRef}
      onMouseMove={handleMouseMove}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      className={`tilt-card-wrapper apple-liquid-glass hover-glass ${isHovered ? 'is-hovered' : ''} ${className}`}
      style={{
        transform: transformStyle,
        transformStyle: 'preserve-3d',
        transition: isHovered ? 'transform 0.08s ease-out' : 'transform 0.5s cubic-bezier(0.2, 0.8, 0.2, 1)',
        position: 'relative',
        borderRadius: '20px',
        ...style,
      }}
      {...props}
    >
      {children}
      {glare && (
        <div
          className="tilt-card-glare"
          style={{
            position: 'absolute',
            inset: 0,
            pointerEvents: 'none',
            borderRadius: 'inherit',
            transition: 'opacity 0.3s ease',
            zIndex: 4,
            mixBlendMode: 'screen',
            ...glareStyle,
          }}
        />
      )}
    </div>
  );
}
