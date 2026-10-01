import React, { useCallback } from 'react';

/**
 * RippleEffect: Wraps any element with Material-style click ripple.
 * Spawns expanding concentric circle from click point with opacity fade.
 */
export default function RippleEffect({
  children,
  className = '',
  style = {},
  color = 'rgba(99, 102, 241, 0.3)',
  duration = 650,
  as: Component = 'div',
  ...props
}) {
  const handleClick = useCallback((e) => {
    const element = e.currentTarget;
    const rect = element.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    const size = Math.max(rect.width, rect.height) * 2.2;

    const ripple = document.createElement('span');
    ripple.className = 'ripple-wave';
    ripple.style.cssText = `
      position: absolute;
      left: ${x - size / 2}px;
      top: ${y - size / 2}px;
      width: ${size}px;
      height: ${size}px;
      border-radius: 50%;
      background: ${color};
      transform: scale(0);
      opacity: 1;
      pointer-events: none;
      animation: rippleExpand ${duration}ms cubic-bezier(0.16, 1, 0.3, 1) forwards;
    `;

    element.appendChild(ripple);

    setTimeout(() => {
      if (ripple.parentNode) {
        ripple.parentNode.removeChild(ripple);
      }
    }, duration);

    // Call original onClick if present
    if (props.onClick) {
      props.onClick(e);
    }
  }, [color, duration, props.onClick]);

  return (
    <Component
      className={`ripple-container ${className}`}
      style={{
        position: 'relative',
        overflow: 'hidden',
        ...style,
      }}
      {...props}
      onClick={handleClick}
    >
      {children}
    </Component>
  );
}
