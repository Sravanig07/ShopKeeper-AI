import React, { useState, useEffect, useRef } from 'react';
import { api } from '../api/client';
import TiltCard from './common/TiltCard';
import Floating3DCube from './common/Floating3DCube';
import AnimatedCounter from './common/AnimatedCounter';
import SkeletonLoader from './common/SkeletonLoader';
import RippleEffect from './common/RippleEffect';
import {
  Boxes,
  DollarSign,
  AlertTriangle,
  TrendingUp,
  Package,
  ShoppingCart,
  ArrowRight,
  Sparkles,
  RefreshCw,
  FileText,
  Zap,
  Activity
} from 'lucide-react';

export default function DashboardView({ onNavigate, showToast }) {
  const [summary, setSummary] = useState(null);
  const [todaySales, setTodaySales] = useState(null);
  const [insights, setInsights] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [scrollY, setScrollY] = useState(0);
  const contentRef = useRef(null);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const [sumData, salesData, insightsData, recsData] = await Promise.all([
        api.inventory.getSummary(),
        api.sales.getTodayAnalytics(),
        api.ai.getInsights(),
        api.ai.getRestockRecommendations()
      ]);
      setSummary(sumData);
      setTodaySales(salesData);
      setInsights(insightsData);
      setRecommendations(recsData || []);
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  // Parallax scroll effect for hero banner
  useEffect(() => {
    const mainWrapper = document.querySelector('.main-wrapper');
    if (!mainWrapper) return;
    
    const handleScroll = () => {
      setScrollY(mainWrapper.scrollTop);
    };
    mainWrapper.addEventListener('scroll', handleScroll, { passive: true });
    return () => mainWrapper.removeEventListener('scroll', handleScroll);
  }, []);

  const criticalItems = recommendations.filter((r) => r.stockoutRiskLevel === 'CRITICAL');

  if (loading) {
    return (
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
          <div>
            <div style={{ width: '200px', height: '24px', borderRadius: '8px', background: 'rgba(148,163,184,0.15)', marginBottom: '8px' }} />
            <div style={{ width: '340px', height: '14px', borderRadius: '6px', background: 'rgba(148,163,184,0.1)' }} />
          </div>
        </div>
        <SkeletonLoader variant="card" count={1} />
        <SkeletonLoader variant="kpi" count={5} />
        <SkeletonLoader variant="card" count={2} />
      </div>
    );
  }

  return (
    <div ref={contentRef}>
      {/* Top Header Controls */}
      <div className="stagger-item" style={{ '--stagger-index': 0, display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 900, color: '#0f172a', letterSpacing: '-0.02em' }}>
            Executive Dashboard
          </h2>
          <p style={{ fontSize: '0.86rem', color: '#64748b' }}>
            Real-time retail inventory intelligence & POS checkout velocity
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={loadDashboardData} className="btn btn-outline" style={{ display: 'flex', gap: '6px' }}>
            <RefreshCw size={16} className={loading ? 'spin' : ''} />
            <span>Refresh</span>
          </button>
          <button onClick={() => onNavigate('pos')} className="btn btn-primary" style={{ display: 'flex', gap: '6px' }}>
            <ShoppingCart size={16} />
            <span>New Sale</span>
          </button>
        </div>
      </div>

      {/* 3D Luxury Hero Banner with Parallax */}
      <div className="stagger-item" style={{ '--stagger-index': 1 }}>
        <TiltCard
          maxTilt={5}
          scale={1.008}
          style={{
            marginBottom: '26px',
            transform: `translateY(${scrollY * -0.08}px)`,
            transition: 'none',
          }}
        >
          <div
            className="apple-aurora-border"
            style={{
              background: 'linear-gradient(135deg, rgba(15, 23, 42, 0.90) 0%, rgba(30, 27, 75, 0.88) 50%, rgba(49, 46, 129, 0.92) 100%)',
              backdropFilter: 'blur(32px) saturate(220%)',
              WebkitBackdropFilter: 'blur(32px) saturate(220%)',
              borderRadius: '24px',
              padding: '26px 32px',
              border: '1px solid rgba(255, 255, 255, 0.18)',
              boxShadow: '0 24px 50px -10px rgba(15, 23, 42, 0.55), 0 0 35px rgba(168, 85, 247, 0.28), inset 0 1.5px 2px rgba(255, 255, 255, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '24px',
              color: '#fff',
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '22px' }}>
              <Floating3DCube size={62} />
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', background: 'rgba(99, 102, 241, 0.3)', border: '1px solid rgba(99, 102, 241, 0.5)', padding: '3px 10px', borderRadius: '9999px', fontSize: '0.72rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#c7d2fe' }}>
                    <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: '#10b981', boxShadow: '0 0 8px #10b981', animation: 'glowPulse 2s ease-in-out infinite' }} />
                    Store AI Active
                  </span>
                  <span style={{ fontSize: '0.78rem', color: '#94a3b8' }}>Heuristic Decision Engine</span>
                </div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 800, letterSpacing: '-0.01em', color: '#ffffff' }}>
                  ShelfIQ Autonomous Retail Intelligence
                </h3>
                <p style={{ fontSize: '0.84rem', color: '#cbd5e1', maxWidth: '580px', marginTop: '3px' }}>
                  Real-time stock velocity, Days of Inventory Remaining (DOIR), dead-stock capital unlocks, and instant 1-click PO restock.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '12px', alignItems: 'center', flexShrink: 0 }}>
              <button
                onClick={() => onNavigate('copilot')}
                className="btn btn-primary"
                style={{
                  background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
                  boxShadow: '0 0 20px rgba(168, 85, 247, 0.45)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  padding: '10px 18px',
                  fontSize: '0.86rem',
                }}
              >
                <Sparkles size={16} />
                <span>Ask AI Copilot</span>
              </button>
            </div>
          </div>
        </TiltCard>
      </div>

      {/* KPI 3D Tilt Cards with AnimatedCounter + Stagger */}
      <div className="kpi-grid">
        <div className="stagger-item" style={{ '--stagger-index': 2 }}>
          <TiltCard maxTilt={10} scale={1.03}>
            <div className="kpi-card" style={{ height: '100%', marginBottom: 0 }}>
              <div className="kpi-info">
                <span>Inventory Valuation</span>
                <h3>
                  <AnimatedCounter value={summary?.totalValue || 0} prefix="₹" duration={2000} />
                </h3>
              </div>
              <div className="kpi-icon-box icon-emerald">
                <DollarSign size={24} />
              </div>
            </div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 3 }}>
          <TiltCard maxTilt={10} scale={1.03}>
            <div className="kpi-card" style={{ height: '100%', marginBottom: 0 }}>
              <div className="kpi-info">
                <span>Active SKUs</span>
                <h3>
                  <AnimatedCounter value={summary?.totalProducts || 0} duration={1600} />
                </h3>
              </div>
              <div className="kpi-icon-box icon-indigo">
                <Boxes size={24} />
              </div>
            </div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 4 }}>
          <TiltCard maxTilt={10} scale={1.03}>
            <div className="kpi-card" style={{ height: '100%', marginBottom: 0 }}>
              <div className="kpi-info">
                <span>In-Stock Units</span>
                <h3>
                  <AnimatedCounter value={summary?.totalUnitsInStock || 0} duration={1800} />
                </h3>
              </div>
              <div className="kpi-icon-box icon-blue">
                <Package size={24} />
              </div>
            </div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 5 }}>
          <TiltCard maxTilt={10} scale={1.03}>
            <div className="kpi-card" style={{ height: '100%', marginBottom: 0 }}>
              <div className="kpi-info">
                <span>Low Stock SKUs</span>
                <h3 style={{ color: summary?.lowStockCount > 0 ? '#e11d48' : '#0f172a' }}>
                  <AnimatedCounter value={summary?.lowStockCount || 0} duration={1400} />
                </h3>
              </div>
              <div className="kpi-icon-box icon-rose">
                <AlertTriangle size={24} />
              </div>
            </div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 6 }}>
          <TiltCard maxTilt={10} scale={1.03}>
            <div className="kpi-card" style={{ height: '100%', marginBottom: 0 }}>
              <div className="kpi-info">
                <span>Today's Revenue</span>
                <h3>
                  <AnimatedCounter value={todaySales?.totalRevenue || 0} prefix="₹" duration={2200} />
                </h3>
              </div>
              <div className="kpi-icon-box icon-amber">
                <TrendingUp size={24} />
              </div>
            </div>
          </TiltCard>
        </div>
      </div>

      {/* AI Critical Restock Alert Banner */}
      {criticalItems.length > 0 && (
        <div className="stagger-item" style={{ '--stagger-index': 7 }}>
          <TiltCard maxTilt={6} scale={1.01} style={{ marginBottom: '26px' }}>
            <div className="alert-banner" style={{ marginBottom: 0 }}>
            <div className="alert-content">
              <AlertTriangle size={26} className="alert-icon" />
              <div className="alert-text">
                <h4>Stockout Warning ({criticalItems.length} Products Critical)</h4>
                <p>
                  {criticalItems.map((item) => (
                    <span key={item.productId} style={{ display: 'block', marginTop: '2px' }}>
                      • <strong>{item.productName}</strong>: {item.currentStock} units left (est. {item.daysOfInventoryRemaining} days remaining). Reorder {item.recommendedOrderQuantity} units from {item.supplierName}.
                    </span>
                  ))}
                </p>
              </div>
            </div>
            <button
              onClick={() => onNavigate('copilot')}
              className="btn btn-danger-outline"
              style={{ whiteSpace: 'nowrap' }}
            >
              <span>Review Restock Plan</span>
              <ArrowRight size={16} />
            </button>
          </div>
          </TiltCard>
        </div>
      )}

      {/* Quick Launchpad & Analytics */}
      <div className="stagger-item" style={{ '--stagger-index': 8, display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        {/* Fast Action Cards & Health */}
        <div className="card">
          <div className="card-header">
            <h3>Store Health & Quick Operations</h3>
            {insights && (
              <span className="badge badge-info" style={{ fontSize: '0.82rem', padding: '6px 12px' }}>
                Health Score: <AnimatedCounter value={insights.storeHealthScore} duration={1200} /> / 100
              </span>
            )}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '14px', marginBottom: '20px' }}>
            {[
              { tab: 'pos', icon: ShoppingCart, color: '#4f46e5', title: 'POS Billing', desc: 'Rapid barcode checkout & invoice receipt' },
              { tab: 'inventory', icon: Boxes, color: '#059669', title: 'Stock Audit', desc: 'Quick +/- adjustment & immutable log' },
              { tab: 'ocr-scanner', icon: FileText, color: '#0284c7', title: 'OCR Bill Scan', desc: 'Auto-extract line items & restock' },
              { tab: 'copilot', icon: Sparkles, color: '#9333ea', title: 'AI Retail Copilot', desc: 'Chat with store intelligence advisor' },
            ].map((action, idx) => (
              <RippleEffect
                key={action.tab}
                as="button"
                onClick={() => onNavigate(action.tab)}
                className="glass-hover-card stagger-item"
                style={{
                  '--stagger-index': 9 + idx,
                  padding: '16px',
                  borderRadius: '16px',
                  border: '1px solid rgba(226,232,240,0.8)',
                  background: 'rgba(255,255,255,0.75)',
                  backdropFilter: 'blur(12px)',
                  textAlign: 'left',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '8px',
                  boxShadow: 'inset 0 1px 1px rgba(255,255,255,0.9), 0 4px 12px rgba(0,0,0,0.03)',
                }}
              >
                <action.icon size={22} color={action.color} />
                <div style={{ fontWeight: 700, fontSize: '0.95rem' }}>{action.title}</div>
                <div style={{ fontSize: '0.78rem', color: '#64748b' }}>{action.desc}</div>
              </RippleEffect>
            ))}
          </div>

          {insights?.strategicTips && (
            <div style={{ background: 'rgba(248,250,252,0.85)', backdropFilter: 'blur(8px)', padding: '16px', borderRadius: '14px', border: '1px solid rgba(226,232,240,0.8)' }}>
              <div style={{ fontWeight: 700, fontSize: '0.88rem', color: '#334155', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Sparkles size={16} color="#9333ea" />
                <span>AI Strategic Optimization Tips</span>
              </div>
              <ul style={{ paddingLeft: '18px', fontSize: '0.84rem', color: '#475569', lineHeight: 1.6 }}>
                {insights.strategicTips.map((tip, idx) => (
                  <li key={idx}>{tip}</li>
                ))}
              </ul>
            </div>
          )}
        </div>

        {/* Today's Sales Leaderboard */}
        <div className="card">
          <div className="card-header">
            <h3>Top Sellers Today</h3>
            <span style={{ fontSize: '0.82rem', color: '#64748b' }}>
              <AnimatedCounter value={todaySales?.transactionCount || 0} duration={1000} /> Invoices
            </span>
          </div>

          {todaySales?.topSoldItems?.length > 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {todaySales.topSoldItems.map((item, idx) => (
                <div
                  key={item.productId}
                  className="stagger-item"
                  style={{
                    '--stagger-index': idx,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    paddingBottom: '10px',
                    borderBottom: '1px solid #f1f5f9'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div style={{
                      width: '26px',
                      height: '26px',
                      borderRadius: '50%',
                      background: idx === 0 ? 'linear-gradient(135deg, #fef3c7, #fde68a)' : '#f1f5f9',
                      color: idx === 0 ? '#b45309' : '#64748b',
                      fontSize: '0.8rem',
                      fontWeight: 700,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: idx === 0 ? '0 0 12px rgba(251,191,36,0.3)' : 'none',
                    }}>
                      {idx + 1}
                    </div>
                    <div>
                      <div style={{ fontSize: '0.88rem', fontWeight: 600 }}>{item.productName}</div>
                      <div style={{ fontSize: '0.74rem', color: '#94a3b8' }}>{item.sku}</div>
                    </div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '0.9rem', fontWeight: 700 }}>
                      ₹<AnimatedCounter value={item.totalRevenue} duration={1400} />
                    </div>
                    <div style={{ fontSize: '0.74rem', color: '#64748b' }}>{item.unitsSold} sold</div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div style={{ textAlign: 'center', padding: '30px 10px', color: '#94a3b8', fontSize: '0.88rem' }}>
              No sales recorded yet today. Launch the POS terminal to process your first invoice!
            </div>
          )}

          <div style={{ marginTop: '18px', paddingTop: '14px', borderTop: '1px solid rgba(226, 232, 240, 0.8)' }}>
            <RippleEffect
              as="button"
              onClick={() => onNavigate('leaderboard')}
              className="btn btn-outline"
              style={{ width: '100%', fontSize: '0.82rem', padding: '9px 14px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}
            >
              <span>View Cross-Territory Leaderboard</span>
              <ArrowRight size={14} />
            </RippleEffect>
          </div>
        </div>
      </div>
    </div>
  );
}
