import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Sparkles, ShieldCheck, UserCheck, Lock, Mail, ArrowRight } from 'lucide-react';
import Floating3DCube from './common/Floating3DCube';
import AmbientGlow from './common/AmbientGlow';
import TiltCard from './common/TiltCard';
import MagneticButton from './common/MagneticButton';
import RippleEffect from './common/RippleEffect';

export default function LoginView() {
  const { login, loginAsOwner, loginAsStaff, loading, error } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) return;
    try {
      await login(email, password);
    } catch (err) {
      // Handled in context
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'radial-gradient(ellipse at top, #1e1b4b 0%, #0f172a 60%, #0b0f19 100%)',
        padding: '24px',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {/* Ambient glowing luminous background */}
      <AmbientGlow />

      <TiltCard
        maxTilt={7}
        scale={1.01}
        style={{
          width: '100%',
          maxWidth: '480px',
          zIndex: 10,
        }}
      >
        <div
          className="apple-liquid-glass apple-aurora-border"
          style={{
            borderRadius: '28px',
            padding: '42px 38px',
            position: 'relative',
          }}
        >
          {/* 3D Floating Isometric Retail Cube */}
          <div className="stagger-item" style={{ '--stagger-index': 0, textAlign: 'center', marginBottom: '24px' }}>
            <div style={{ margin: '0 auto 18px', display: 'flex', justifyContent: 'center' }}>
              <Floating3DCube size={68} />
            </div>

            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.12), rgba(168, 85, 247, 0.12))', border: '1px solid rgba(99, 102, 241, 0.25)', borderRadius: '9999px', padding: '4px 12px', marginBottom: '10px' }}>
              <Sparkles size={14} color="#6366f1" />
              <span style={{ fontSize: '0.74rem', fontWeight: 700, color: '#4f46e5', letterSpacing: '0.04em', textTransform: 'uppercase' }}>Next-Gen Retail AI</span>
            </div>

            <h1
              style={{
                fontSize: '1.9rem',
                fontWeight: 900,
                letterSpacing: '-0.03em',
                background: 'linear-gradient(135deg, #0f172a 0%, #312e81 60%, #4338ca 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
              }}
            >
              ShelfIQ
            </h1>
            <p style={{ fontSize: '0.86rem', color: '#64748b', marginTop: '4px', fontWeight: 500 }}>
              Autonomous Retail Intelligence & Real-Time POS Engine
            </p>
          </div>

          {error && (
            <div
              className="stagger-item"
              style={{
                '--stagger-index': 1,
                background: 'rgba(254, 242, 242, 0.9)',
                border: '1px solid #fecdd3',
                color: '#e11d48',
                padding: '12px 16px',
                borderRadius: '12px',
                fontSize: '0.86rem',
                marginBottom: '20px',
                boxShadow: '0 4px 12px rgba(225, 29, 72, 0.1)',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
              }}
            >
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div className="stagger-item" style={{ '--stagger-index': 1 }}>
              <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 700, color: '#334155', marginBottom: '6px' }}>
                Email Address
              </label>
              <div style={{ position: 'relative' }}>
                <Mail size={17} color="#94a3b8" style={{ position: 'absolute', left: '16px', top: '50%', transform: 'translateY(-50%)', zIndex: 1 }} />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="owner@shelfiq.io"
                  className="input-glow-focus"
                  style={{
                    width: '100%',
                    padding: '13px 16px 13px 46px',
                    borderRadius: '9999px',
                    border: '1px solid rgba(226, 232, 240, 0.9)',
                    background: 'rgba(255, 255, 255, 0.85)',
                    backdropFilter: 'blur(20px)',
                    fontSize: '0.94rem',
                    boxShadow: 'inset 0 1.5px 2px rgba(0, 0, 0, 0.04)',
                  }}
                />
              </div>
            </div>

            <div className="stagger-item" style={{ '--stagger-index': 2 }}>
              <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 700, color: '#334155', marginBottom: '6px' }}>
                Password
              </label>
              <div style={{ position: 'relative' }}>
                <Lock size={17} color="#94a3b8" style={{ position: 'absolute', left: '16px', top: '50%', transform: 'translateY(-50%)', zIndex: 1 }} />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  className="input-glow-focus"
                  style={{
                    width: '100%',
                    padding: '13px 16px 13px 46px',
                    borderRadius: '9999px',
                    border: '1px solid rgba(226, 232, 240, 0.9)',
                    background: 'rgba(255, 255, 255, 0.85)',
                    backdropFilter: 'blur(20px)',
                    fontSize: '0.94rem',
                    boxShadow: 'inset 0 1.5px 2px rgba(0, 0, 0, 0.04)',
                  }}
                />
              </div>
            </div>

            <div className="stagger-item" style={{ '--stagger-index': 3 }}>
              <MagneticButton
                type="submit"
                disabled={loading}
                className="btn btn-primary"
                style={{
                  width: '100%',
                  padding: '13px',
                  marginTop: '8px',
                  borderRadius: '9999px',
                  fontSize: '0.96rem',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px',
                }}
              >
                <span>{loading ? 'Authenticating...' : 'Sign In to Hub'}</span>
                <ArrowRight size={17} />
              </MagneticButton>
            </div>
          </form>

          {/* Divider */}
          <div
            className="stagger-item"
            style={{
              '--stagger-index': 4,
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              margin: '26px 0 18px',
              color: '#94a3b8',
              fontSize: '0.74rem',
              textTransform: 'uppercase',
              letterSpacing: '0.08em',
              fontWeight: 700,
            }}
          >
            <div style={{ flex: 1, height: '1px', background: 'rgba(226, 232, 240, 0.8)' }} />
            <span>1-Click Fast Login</span>
            <div style={{ flex: 1, height: '1px', background: 'rgba(226, 232, 240, 0.8)' }} />
          </div>

          {/* Quick Demo Access Buttons with Ripple + Glass Hover */}
          <div className="stagger-item" style={{ '--stagger-index': 5, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <RippleEffect
              as="button"
              type="button"
              onClick={loginAsOwner}
              disabled={loading}
              className="btn btn-outline glass-hover-card"
              color="rgba(79, 70, 229, 0.2)"
              style={{
                padding: '12px 14px',
                borderRadius: '9999px',
                fontSize: '0.84rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
                background: 'linear-gradient(135deg, rgba(238, 242, 255, 0.9), rgba(255, 255, 255, 0.95))',
                border: '1px solid rgba(199, 210, 254, 0.9)',
                boxShadow: '0 4px 14px rgba(99, 102, 241, 0.12), inset 0 1px 1.5px rgba(255, 255, 255, 1)',
              }}
            >
              <ShieldCheck size={18} color="#4f46e5" />
              <span>Store Owner</span>
            </RippleEffect>

            <RippleEffect
              as="button"
              type="button"
              onClick={loginAsStaff}
              disabled={loading}
              className="btn btn-outline glass-hover-card"
              color="rgba(16, 185, 129, 0.2)"
              style={{
                padding: '12px 14px',
                borderRadius: '9999px',
                fontSize: '0.84rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
                background: 'linear-gradient(135deg, rgba(236, 253, 245, 0.9), rgba(255, 255, 255, 0.95))',
                border: '1px solid rgba(167, 243, 208, 0.9)',
                boxShadow: '0 4px 14px rgba(16, 185, 129, 0.12), inset 0 1px 1.5px rgba(255, 255, 255, 1)',
              }}
            >
              <UserCheck size={18} color="#059669" />
              <span>Cashier Staff</span>
            </RippleEffect>
          </div>

          <div className="stagger-item" style={{ '--stagger-index': 6, marginTop: '22px', textAlign: 'center', fontSize: '0.78rem', color: '#94a3b8' }}>
            <span>Administrator access: </span>
            <code style={{ background: '#f1f5f9', padding: '2px 6px', borderRadius: '4px', color: '#334155' }}>owner@shelfiq.io</code> / <code style={{ background: '#f1f5f9', padding: '2px 6px', borderRadius: '4px', color: '#334155' }}>Password123!</code>
          </div>
        </div>
      </TiltCard>
    </div>
  );
}
