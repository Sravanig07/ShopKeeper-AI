import React, { useState, useEffect, useRef } from 'react';

/**
 * AnimatedCounter: Rolls numbers from 0 → target with spring easing.
 * Uses requestAnimationFrame for smooth 60fps digit animation.
 */
export default function AnimatedCounter({
  value = 0,
  duration = 1800,
  prefix = '',
  suffix = '',
  decimals = 0,
  className = '',
  style = {},
}) {
  const [displayValue, setDisplayValue] = useState(0);
  const startTimeRef = useRef(null);
  const startValueRef = useRef(0);
  const rafRef = useRef(null);
  const prevValueRef = useRef(0);

  useEffect(() => {
    const targetValue = typeof value === 'number' ? value : parseFloat(value) || 0;
    startValueRef.current = prevValueRef.current;
    startTimeRef.current = null;

    const easeOutExpo = (t) => (t === 1 ? 1 : 1 - Math.pow(2, -10 * t));

    const animate = (timestamp) => {
      if (!startTimeRef.current) startTimeRef.current = timestamp;
      const elapsed = timestamp - startTimeRef.current;
      const progress = Math.min(elapsed / duration, 1);
      const easedProgress = easeOutExpo(progress);

      const current = startValueRef.current + (targetValue - startValueRef.current) * easedProgress;
      setDisplayValue(current);

      if (progress < 1) {
        rafRef.current = requestAnimationFrame(animate);
      } else {
        setDisplayValue(targetValue);
        prevValueRef.current = targetValue;
      }
    };

    rafRef.current = requestAnimationFrame(animate);

    return () => {
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
    };
  }, [value, duration]);

  const formatNumber = (num) => {
    if (decimals > 0) {
      return num.toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }
    return Math.round(num).toLocaleString();
  };

  return (
    <span
      className={`animated-counter ${className}`}
      style={{
        display: 'inline-block',
        fontVariantNumeric: 'tabular-nums',
        ...style,
      }}
    >
      {prefix}{formatNumber(displayValue)}{suffix}
    </span>
  );
}
