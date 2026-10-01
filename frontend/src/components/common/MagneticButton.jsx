import React, { useState, useRef, useCallback } from 'react';

/**
 * MagneticButton: Button that subtly pulls toward the cursor on hover
 * with elastic snap-back on mouse leave. Adds a premium spatial feel.
 */
export default function MagneticButton({
  children,
  className = '',
  style = {},
  strength = 0.35,
  radius = 120,
  ...props
}) {
  const btnRef = useRef(null);
  const [offset, setOffset] = useState({ x: 0, y: 0 });
  const [isHovered, setIsHovered] = useState(false);

  const handleMouseMove = useCallback((e) => {
    if (!btnRef.current) return;
    const rect = btnRef.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;

    const distX = e.clientX - centerX;
    const distY = e.clientY - centerY;
    const dist = Math.sqrt(distX * distX + distY * distY);

    if (dist < radius) {
      setOffset({
        x: distX * strength,
        y: distY * strength,
      });
    }
  }, [strength, radius]);

  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
    setOffset({ x: 0, y: 0 });
  };

  return (
    <button
      ref={btnRef}
      className={`magnetic-btn ${className}`}
      onMouseMove={handleMouseMove}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={{
        transform: `translate(${offset.x}px, ${offset.y}px)`,
        transition: isHovered
          ? 'transform 0.15s cubic-bezier(0.23, 1, 0.32, 1)'
          : 'transform 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
        willChange: 'transform',
        ...style,
      }}
      {...props}
    >
      {children}
    </button>
  );
}
