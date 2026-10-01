import React, { useState, useEffect, useRef } from 'react';

/**
 * PageTransition: Wraps page content with staggered fade-slide animation.
 * Children mount with a smooth upward slide + fade, unmount gracefully.
 */
export default function PageTransition({ children, activeKey, className = '' }) {
  const [rendered, setRendered] = useState(children);
  const [phase, setPhase] = useState('entered'); // 'entering' | 'entered' | 'exiting'
  const timeoutRef = useRef(null);

  useEffect(() => {
    // Exit current, then enter new
    setPhase('exiting');
    
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    
    timeoutRef.current = setTimeout(() => {
      setRendered(children);
      setPhase('entering');

      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          setPhase('entered');
        });
      });
    }, 220); // exit duration

    return () => {
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    };
  }, [activeKey]);

  // On first mount, animate in
  useEffect(() => {
    setPhase('entering');
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        setPhase('entered');
      });
    });
  }, []);

  return (
    <div
      className={`page-transition ${phase} ${className}`}
      style={{
        transition: 'opacity 0.35s cubic-bezier(0.16, 1, 0.3, 1), transform 0.35s cubic-bezier(0.16, 1, 0.3, 1)',
        opacity: phase === 'entered' ? 1 : 0,
        transform: phase === 'entered' ? 'translateY(0) scale(1)' : phase === 'exiting' ? 'translateY(-12px) scale(0.99)' : 'translateY(18px) scale(0.99)',
        willChange: 'opacity, transform',
      }}
    >
      {rendered}
    </div>
  );
}
