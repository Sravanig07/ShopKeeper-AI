import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import TiltCard from './common/TiltCard';
import AnimatedCounter from './common/AnimatedCounter';
import SkeletonLoader from './common/SkeletonLoader';
import RippleEffect from './common/RippleEffect';
import MagneticButton from './common/MagneticButton';
import {
  Trophy,
  Crown,
  Medal,
  MapPin,
  Building,
  Globe,
  Boxes,
  TrendingUp,
  Flame,
  ArrowUpRight,
  Filter,
  Calendar,
  Sparkles,
  Layers,
  ChevronRight,
  ChevronDown
} from 'lucide-react';

const ALL_INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh',
  'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand',
  'Karnataka', 'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur',
  'Meghalaya', 'Mizoram', 'Nagaland', 'Odisha', 'Punjab',
  'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura',
  'Uttar Pradesh', 'Uttarakhand', 'West Bengal'
];

const UNION_TERRITORIES = [
  'Delhi NCR', 'Jammu and Kashmir', 'Ladakh', 'Chandigarh', 'Puducherry'
];

const FEATURED_STATES = [
  'Karnataka', 'Maharashtra', 'Delhi NCR', 'Tamil Nadu', 'Telangana',
  'Gujarat', 'Uttar Pradesh', 'West Bengal', 'Kerala', 'Rajasthan', 'Punjab'
];

export default function LeaderboardView({ showToast }) {
  const [groupBy, setGroupBy] = useState('CITY'); // CITY, STATE, REGION
  const [type, setType] = useState('ITEM'); // ITEM, CATEGORY
  const [timeRange, setTimeRange] = useState('TODAY'); // TODAY, WEEK, MONTH, ALL
  const [leaderboardData, setLeaderboardData] = useState(null);
  const [selectedGeoName, setSelectedGeoName] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchLeaderboard = async () => {
    setLoading(true);
    try {
      const data = await api.sales.getLeaderboard(groupBy, type, timeRange);
      setLeaderboardData(data);
      if (data?.groups?.length > 0) {
        // Keep selected if exists in new groups, else default to first
        const exists = data.groups.some(g => g.geoName === selectedGeoName);
        if (!exists) {
          setSelectedGeoName(data.groups[0].geoName);
        }
      }
    } catch (err) {
      if (showToast) showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaderboard();
  }, [groupBy, type, timeRange]);

  const activeGroup = leaderboardData?.groups?.find(g => g.geoName === selectedGeoName) || leaderboardData?.groups?.[0];
  const rankings = activeGroup?.rankings || [];

  const topThree = rankings.slice(0, 3);
  const remainingRankings = rankings.slice(3);

  // Compute visible quick-filter pills
  const groupsList = leaderboardData?.groups || [];
  let visiblePills = [];
  if (groupBy === 'STATE') {
    visiblePills = groupsList.filter(g => FEATURED_STATES.includes(g.geoName));
    if (activeGroup && !FEATURED_STATES.includes(activeGroup.geoName)) {
      visiblePills = [activeGroup, ...visiblePills];
    }
  } else {
    visiblePills = groupsList;
  }

  const getMedalColor = (rank) => {
    switch (rank) {
      case 1:
        return {
          gradient: 'linear-gradient(135deg, rgba(254, 240, 138, 0.95), rgba(253, 224, 71, 0.95))',
          shadow: '0 0 24px rgba(234, 179, 8, 0.45)',
          border: 'rgba(234, 179, 8, 0.6)',
          textColor: '#854d0e',
          badgeText: 'GOLD 1ST'
        };
      case 2:
        return {
          gradient: 'linear-gradient(135deg, rgba(241, 245, 249, 0.95), rgba(226, 232, 240, 0.95))',
          shadow: '0 0 20px rgba(148, 163, 184, 0.35)',
          border: 'rgba(148, 163, 184, 0.6)',
          textColor: '#334155',
          badgeText: 'SILVER 2ND'
        };
      case 3:
        return {
          gradient: 'linear-gradient(135deg, rgba(254, 215, 170, 0.95), rgba(251, 146, 60, 0.9))',
          shadow: '0 0 20px rgba(249, 115, 22, 0.35)',
          border: 'rgba(249, 115, 22, 0.6)',
          textColor: '#7c2d12',
          badgeText: 'BRONZE 3RD'
        };
      default:
        return {
          gradient: 'linear-gradient(135deg, #f1f5f9, #e2e8f0)',
          shadow: 'none',
          border: 'transparent',
          textColor: '#64748b',
          badgeText: `#${rank}`
        };
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '26px' }}>
      {/* Hero Header with Apple Aurora Glow */}
      <div className="stagger-item" style={{ '--stagger-index': 0 }}>
        <TiltCard maxTilt={5} scale={1.008}>
          <div
            className="apple-aurora-border"
            style={{
              background: 'linear-gradient(135deg, rgba(15, 23, 42, 0.92) 0%, rgba(30, 27, 75, 0.90) 50%, rgba(67, 56, 202, 0.94) 100%)',
              backdropFilter: 'blur(32px) saturate(220%)',
              WebkitBackdropFilter: 'blur(32px) saturate(220%)',
              borderRadius: '24px',
              padding: '26px 32px',
              border: '1px solid rgba(255, 255, 255, 0.2)',
              boxShadow: '0 24px 50px -10px rgba(15, 23, 42, 0.55), 0 0 35px rgba(168, 85, 247, 0.3), inset 0 1.5px 2px rgba(255, 255, 255, 0.3)',
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
              <div
                style={{
                  width: '64px',
                  height: '64px',
                  borderRadius: '18px',
                  background: 'linear-gradient(135deg, #fbbf24, #f59e0b)',
                  color: '#78350f',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 0 30px rgba(251, 191, 36, 0.5)',
                  flexShrink: 0,
                }}
              >
                <Trophy size={32} />
              </div>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', background: 'rgba(251, 191, 36, 0.25)', border: '1px solid rgba(251, 191, 36, 0.5)', padding: '3px 10px', borderRadius: '9999px', fontSize: '0.72rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#fef08a' }}>
                    <Flame size={12} color="#fbbf24" />
                    Market Velocity Rankings
                  </span>
                  <span style={{ fontSize: '0.78rem', color: '#cbd5e1' }}>Multi-Regional Sales Intelligence</span>
                </div>
                <h3 style={{ fontSize: '1.4rem', fontWeight: 800, letterSpacing: '-0.01em', color: '#ffffff' }}>
                  Cross-Territory Retail Leaderboard
                </h3>
                <p style={{ fontSize: '0.84rem', color: '#cbd5e1', maxWidth: '620px', marginTop: '3px' }}>
                  Benchmark your fastest moving SKUs and top revenue product categories across Metro Cities, States, and Macro Geographic Zones.
                </p>
              </div>
            </div>

            {/* Overall Aggregate Stats in Hero */}
            {activeGroup && (
              <div style={{ display: 'flex', gap: '16px', alignItems: 'center', flexShrink: 0 }}>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '0.74rem', color: '#cbd5e1', textTransform: 'uppercase', fontWeight: 700 }}>
                    {activeGroup.geoName} Volume
                  </div>
                  <div style={{ fontSize: '1.3rem', fontWeight: 800, color: '#f8fafc' }}>
                    <AnimatedCounter value={activeGroup.totalUnits} duration={1200} /> units
                  </div>
                  <div style={{ fontSize: '0.84rem', color: '#a7f3d0', fontWeight: 700 }}>
                    ₹<AnimatedCounter value={activeGroup.totalRevenue || 0} duration={1400} />
                  </div>
                </div>
              </div>
            )}
          </div>
        </TiltCard>
      </div>

      {/* Control Bar: Entity (Item vs Category) + Scope (City, State, Region) + Timeframe */}
      <div className="card stagger-item" style={{ '--stagger-index': 1, padding: '18px 24px', marginBottom: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '20px', flexWrap: 'wrap' }}>
          {/* Entity Toggle: Items vs Categories */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Ranking:
            </span>
            <div style={{ display: 'inline-flex', background: 'rgba(241, 245, 249, 0.9)', padding: '4px', borderRadius: '9999px', border: '1px solid rgba(226, 232, 240, 0.9)' }}>
              <RippleEffect
                as="button"
                onClick={() => setType('ITEM')}
                style={{
                  padding: '7px 16px',
                  borderRadius: '9999px',
                  fontSize: '0.82rem',
                  fontWeight: 700,
                  border: 'none',
                  background: type === 'ITEM' ? 'linear-gradient(135deg, #4f46e5, #6366f1)' : 'transparent',
                  color: type === 'ITEM' ? '#fff' : '#475569',
                  boxShadow: type === 'ITEM' ? '0 2px 8px rgba(79, 70, 229, 0.35)' : 'none',
                }}
              >
                Top Items (SKUs)
              </RippleEffect>
              <RippleEffect
                as="button"
                onClick={() => setType('CATEGORY')}
                style={{
                  padding: '7px 16px',
                  borderRadius: '9999px',
                  fontSize: '0.82rem',
                  fontWeight: 700,
                  border: 'none',
                  background: type === 'CATEGORY' ? 'linear-gradient(135deg, #4f46e5, #6366f1)' : 'transparent',
                  color: type === 'CATEGORY' ? '#fff' : '#475569',
                  boxShadow: type === 'CATEGORY' ? '0 2px 8px rgba(79, 70, 229, 0.35)' : 'none',
                }}
              >
                Top Categories
              </RippleEffect>
            </div>
          </div>

          {/* Scope Selector: City / State / Region */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Territory:
            </span>
            <div style={{ display: 'inline-flex', background: 'rgba(241, 245, 249, 0.9)', padding: '4px', borderRadius: '9999px', border: '1px solid rgba(226, 232, 240, 0.9)' }}>
              {[
                { id: 'CITY', label: 'Cities', icon: Building },
                { id: 'STATE', label: 'States', icon: MapPin },
                { id: 'REGION', label: 'Macro Regions', icon: Globe },
              ].map((s) => {
                const Icon = s.icon;
                const active = groupBy === s.id;
                return (
                  <RippleEffect
                    key={s.id}
                    as="button"
                    onClick={() => setGroupBy(s.id)}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '6px',
                      padding: '7px 14px',
                      borderRadius: '9999px',
                      fontSize: '0.82rem',
                      fontWeight: 700,
                      border: 'none',
                      background: active ? '#ffffff' : 'transparent',
                      color: active ? '#4f46e5' : '#475569',
                      boxShadow: active ? '0 2px 8px rgba(0, 0, 0, 0.08)' : 'none',
                    }}
                  >
                    <Icon size={14} />
                    <span>{s.label}</span>
                  </RippleEffect>
                );
              })}
            </div>
          </div>

          {/* Timeframe Selector */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Period:
            </span>
            <div style={{ display: 'inline-flex', background: 'rgba(241, 245, 249, 0.9)', padding: '4px', borderRadius: '9999px', border: '1px solid rgba(226, 232, 240, 0.9)' }}>
              {['TODAY', 'WEEK', 'MONTH', 'ALL'].map((tr) => (
                <RippleEffect
                  key={tr}
                  as="button"
                  onClick={() => setTimeRange(tr)}
                  style={{
                    padding: '6px 12px',
                    borderRadius: '9999px',
                    fontSize: '0.78rem',
                    fontWeight: 700,
                    border: 'none',
                    background: timeRange === tr ? '#0f172a' : 'transparent',
                    color: timeRange === tr ? '#fff' : '#64748b',
                  }}
                >
                  {tr === 'TODAY' ? 'Today' : tr === 'WEEK' ? '7 Days' : tr === 'MONTH' ? '30 Days' : 'All-Time'}
                </RippleEffect>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Territory Selector & State Dropdown Panel */}
      <div className="card hover-glass stagger-item" style={{ '--stagger-index': 2, padding: '16px 20px', marginBottom: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '16px', flexWrap: 'wrap', marginBottom: '14px' }}>
          {/* Dropdown with Icon and Label */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', flex: '1 1 320px', minWidth: '280px' }}>
            <div
              style={{
                width: '40px',
                height: '40px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.18), rgba(168, 85, 247, 0.22))',
                border: '1px solid rgba(99, 102, 241, 0.35)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#4f46e5',
                flexShrink: 0,
                boxShadow: '0 4px 12px rgba(99, 102, 241, 0.15)'
              }}
            >
              {groupBy === 'STATE' ? <MapPin size={20} /> : groupBy === 'CITY' ? <Building size={20} /> : <Globe size={20} />}
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                <span style={{ fontSize: '0.86rem', fontWeight: 800, color: '#0f172a' }}>
                  {groupBy === 'STATE' ? 'Select Indian State / Territory' : groupBy === 'CITY' ? 'Select Indian City' : 'Select Macro Geographic Region'}
                </span>
                {groupBy === 'STATE' && (
                  <span style={{ fontSize: '0.7rem', fontWeight: 700, padding: '2px 8px', borderRadius: '9999px', background: 'rgba(99, 102, 241, 0.12)', color: '#4f46e5', border: '1px solid rgba(99, 102, 241, 0.28)' }}>
                    28 States + 5 UTs
                  </span>
                )}
              </div>
              <div style={{ position: 'relative' }}>
                <select
                  value={selectedGeoName}
                  onChange={(e) => setSelectedGeoName(e.target.value)}
                  className="input-glow-focus hover-glass"
                  style={{
                    width: '100%',
                    padding: '9px 38px 9px 14px',
                    borderRadius: '12px',
                    border: '1.5px solid rgba(99, 102, 241, 0.35)',
                    background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98))',
                    backdropFilter: 'blur(20px)',
                    WebkitBackdropFilter: 'blur(20px)',
                    fontSize: '0.88rem',
                    fontWeight: 700,
                    color: '#0f172a',
                    cursor: 'pointer',
                    appearance: 'none',
                    boxShadow: '0 2px 10px rgba(99, 102, 241, 0.08)',
                    transition: 'all 0.2s ease',
                  }}
                >
                  {groupBy === 'STATE' ? (
                    <>
                      <optgroup label="✨ Featured & High-Volume States">
                        {FEATURED_STATES.filter(s => groupsList.some(g => g.geoName === s)).map(s => (
                          <option key={s} value={s}>📍 {s}</option>
                        ))}
                      </optgroup>
                      <optgroup label="🇮🇳 All 28 Indian States (A-Z)">
                        {ALL_INDIAN_STATES.filter(s => groupsList.some(g => g.geoName === s)).map(s => (
                          <option key={s} value={s}>🇮🇳 {s}</option>
                        ))}
                      </optgroup>
                      <optgroup label="🏛️ Union Territories">
                        {UNION_TERRITORIES.filter(s => groupsList.some(g => g.geoName === s)).map(s => (
                          <option key={s} value={s}>🏛️ {s}</option>
                        ))}
                      </optgroup>
                    </>
                  ) : (
                    groupsList.map(g => (
                      <option key={g.geoName} value={g.geoName}>
                        {groupBy === 'CITY' ? '🏙️ ' : '🌐 '}{g.geoName}
                      </option>
                    ))
                  )}
                </select>
                <ChevronDown
                  size={16}
                  color="#4f46e5"
                  style={{
                    position: 'absolute',
                    right: '12px',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    pointerEvents: 'none'
                  }}
                />
              </div>
            </div>
          </div>

          {/* Active selection summary badge */}
          {activeGroup && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', background: 'rgba(241, 245, 249, 0.85)', padding: '8px 16px', borderRadius: '14px', border: '1px solid rgba(226, 232, 240, 0.9)', boxShadow: '0 2px 6px rgba(0, 0, 0, 0.02)' }}>
              <div>
                <div style={{ fontSize: '0.72rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 700 }}>
                  Active Leaderboard Market
                </div>
                <div style={{ fontSize: '0.95rem', fontWeight: 800, color: '#0f172a' }}>
                  {activeGroup.geoName}
                </div>
              </div>
              <div style={{ width: '1px', height: '24px', background: '#cbd5e1' }} />
              <div>
                <div style={{ fontSize: '0.72rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 700 }}>
                  Ranked Entities
                </div>
                <div style={{ fontSize: '0.95rem', fontWeight: 800, color: '#4f46e5' }}>
                  {activeGroup.rankings?.length || 0} {type === 'ITEM' ? 'SKUs' : 'Categories'}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Quick Access Filter Pills */}
        <div style={{ display: 'flex', gap: '8px', overflowX: 'auto', paddingBottom: '4px', alignItems: 'center' }}>
          <span style={{ fontSize: '0.72rem', fontWeight: 700, color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.04em', whiteSpace: 'nowrap', marginRight: '4px' }}>
            Quick Filter:
          </span>
          {visiblePills.map((g) => {
            const isSelected = g.geoName === selectedGeoName;
            return (
              <RippleEffect
                key={g.geoName}
                as="button"
                onClick={() => setSelectedGeoName(g.geoName)}
                className="chip glass-hover-card"
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  padding: '7px 14px',
                  borderRadius: '9999px',
                  fontSize: '0.8rem',
                  fontWeight: 700,
                  border: isSelected ? '1px solid #4f46e5' : '1px solid rgba(203, 213, 225, 0.7)',
                  background: isSelected
                    ? 'linear-gradient(135deg, rgba(79, 70, 229, 0.95), rgba(99, 102, 241, 0.95))'
                    : 'rgba(255, 255, 255, 0.85)',
                  color: isSelected ? '#ffffff' : '#334155',
                  boxShadow: isSelected
                    ? '0 4px 14px rgba(79, 70, 229, 0.35), inset 0 1px 1px rgba(255, 255, 255, 0.6)'
                    : '0 2px 6px rgba(0, 0, 0, 0.03)',
                  whiteSpace: 'nowrap',
                  cursor: 'pointer'
                }}
              >
                <MapPin size={12} color={isSelected ? '#fef08a' : '#6366f1'} />
                <span>{g.geoName}</span>
                <span
                  style={{
                    fontSize: '0.68rem',
                    padding: '1px 6px',
                    borderRadius: '9999px',
                    background: isSelected ? 'rgba(255, 255, 255, 0.22)' : '#f1f5f9',
                    color: isSelected ? '#ffffff' : '#64748b',
                  }}
                >
                  ₹{(g.totalRevenue / 1000).toFixed(0)}k
                </span>
              </RippleEffect>
            );
          })}
        </div>
      </div>

      {loading ? (
        <div>
          <SkeletonLoader variant="kpi" count={3} />
          <SkeletonLoader variant="table" count={5} />
        </div>
      ) : (
        <>
          {/* Top 3 Podium (Gold, Silver, Bronze) - Rendered only when sales exist */}
          {topThree.length > 0 ? (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px' }}>
              {topThree.map((item, idx) => {
                const medal = getMedalColor(item.rank);
                return (
                  <div key={item.id} className="stagger-item" style={{ '--stagger-index': 3 + idx }}>
                  <TiltCard maxTilt={8} scale={1.025}>
                    <div
                      className="card glass-hover-card"
                      style={{
                        padding: '24px',
                        marginBottom: 0,
                        position: 'relative',
                        overflow: 'hidden',
                        border: `1.5px solid ${medal.border}`,
                        boxShadow: `0 14px 34px -8px rgba(0,0,0,0.08), ${medal.shadow}`,
                      }}
                    >
                      {/* Top Rank Badge */}
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '14px' }}>
                        <div
                          style={{
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '6px',
                            background: medal.gradient,
                            color: medal.textColor,
                            padding: '4px 12px',
                            borderRadius: '9999px',
                            fontWeight: 800,
                            fontSize: '0.76rem',
                            letterSpacing: '0.04em',
                            boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                          }}
                        >
                          {item.rank === 1 && <Crown size={14} />}
                          {item.rank === 2 && <Medal size={14} />}
                          {item.rank === 3 && <Medal size={14} />}
                          <span>{medal.badgeText}</span>
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#16a34a', fontSize: '0.78rem', fontWeight: 700 }}>
                          <TrendingUp size={14} />
                          <span>{item.growthRate > 0 ? `+${item.growthRate}%` : `${item.growthRate}%`}</span>
                        </div>
                      </div>

                      {/* Item Details */}
                      <div>
                        <h4 style={{ fontSize: '1.05rem', fontWeight: 800, color: '#0f172a', marginBottom: '4px', lineHeight: 1.3 }}>
                          {item.name}
                        </h4>
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center', fontSize: '0.74rem', color: '#64748b' }}>
                          <span className="badge badge-muted" style={{ padding: '2px 8px', fontSize: '0.7rem' }}>
                            {item.category}
                          </span>
                          {item.sku && <span>SKU: {item.sku}</span>}
                        </div>
                      </div>

                      {/* Revenue & Units */}
                      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginTop: '18px', paddingTop: '14px', borderTop: '1px solid rgba(226, 232, 240, 0.8)' }}>
                        <div>
                          <div style={{ fontSize: '0.72rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Total Revenue</div>
                          <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', marginTop: '2px' }}>
                            ₹<AnimatedCounter value={item.revenue || 0} duration={1400} />
                          </div>
                        </div>
                        <div>
                          <div style={{ fontSize: '0.72rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 600 }}>Units Sold</div>
                          <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#4f46e5', marginTop: '2px' }}>
                            <AnimatedCounter value={item.unitsSold || 0} duration={1200} />
                          </div>
                        </div>
                      </div>

                      {/* Market Share Progress Bar */}
                      <div style={{ marginTop: '16px' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.74rem', fontWeight: 600, color: '#64748b', marginBottom: '6px' }}>
                          <span>Market Territory Share</span>
                          <span style={{ color: '#0f172a', fontWeight: 800 }}>{item.marketSharePercentage}%</span>
                        </div>
                        <div style={{ width: '100%', height: '7px', borderRadius: '9999px', background: '#f1f5f9', overflow: 'hidden' }}>
                          <div
                            style={{
                              width: `${Math.min(item.marketSharePercentage, 100)}%`,
                              height: '100%',
                              borderRadius: '9999px',
                              background: item.rank === 1
                                ? 'linear-gradient(90deg, #fbbf24, #f59e0b)'
                                : 'linear-gradient(90deg, #6366f1, #a855f7)',
                              transition: 'width 1.2s cubic-bezier(0.16, 1, 0.3, 1)',
                            }}
                          />
                        </div>
                      </div>
                    </div>
                  </TiltCard>
                </div>
              );
            })}
          </div>
        ) : null}

        {/* Full Territory Rankings Table */}
        <div className="card stagger-item" style={{ '--stagger-index': 6, padding: 0 }}>
          <div className="card-header" style={{ padding: '20px 26px', borderBottom: '1px solid rgba(226, 232, 240, 0.8)', margin: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <Layers size={20} color="#4f46e5" />
              <div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: 800 }}>
                  {activeGroup?.geoName} — {type === 'ITEM' ? 'Top Selling Items' : 'Top Performing Categories'}
                </h3>
                <span style={{ fontSize: '0.78rem', color: '#64748b' }}>
                  Ranked by cumulative POS checkout receipts and store transaction velocity
                </span>
              </div>
            </div>
            <span className="badge badge-info" style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
              {rankings.length} Positions Tracked
            </span>
          </div>

          <div className="table-wrapper" style={{ border: 'none' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th style={{ width: '70px', textAlign: 'center' }}>Rank</th>
                  <th>{type === 'ITEM' ? 'Product & SKU' : 'Product Category'}</th>
                  <th>Territory Hub</th>
                  <th style={{ textAlign: 'center' }}>Units Sold</th>
                  <th>Gross Revenue</th>
                  <th style={{ width: '180px' }}>Market Share</th>
                  <th style={{ textAlign: 'right' }}>Velocity Trend</th>
                </tr>
              </thead>
              <tbody>
                {rankings.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '48px 24px', color: '#94a3b8' }}>
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                        <Trophy size={28} color="#cbd5e1" />
                        <span style={{ fontWeight: 700, color: '#64748b', fontSize: '0.92rem' }}>
                          No sales transactions recorded yet for {activeGroup?.geoName}
                        </span>
                        <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                          Complete your first POS checkout to compute live retail sales velocity!
                        </span>
                      </div>
                    </td>
                  </tr>
                ) : (
                    rankings.map((it, idx) => {
                      return (
                        <tr key={it.id || idx} className="stagger-item hover-glass" style={{ '--stagger-index': Math.min(idx, 15) }}>
                          <td style={{ textAlign: 'center' }}>
                            <span
                              style={{
                                display: 'inline-flex',
                                width: '28px',
                                height: '28px',
                                borderRadius: '50%',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontWeight: 800,
                                fontSize: '0.82rem',
                                background: it.rank === 1
                                  ? 'linear-gradient(135deg, #fef08a, #facc15)'
                                  : it.rank === 2
                                  ? 'linear-gradient(135deg, #f1f5f9, #cbd5e1)'
                                  : it.rank === 3
                                  ? 'linear-gradient(135deg, #fed7aa, #fb923c)'
                                  : '#f1f5f9',
                                color: it.rank === 1 ? '#854d0e' : it.rank === 2 ? '#334155' : it.rank === 3 ? '#7c2d12' : '#64748b',
                                boxShadow: it.rank <= 3 ? '0 2px 8px rgba(0,0,0,0.1)' : 'none',
                              }}
                            >
                              {it.rank}
                            </span>
                          </td>
                          <td>
                            <div style={{ fontWeight: 700, fontSize: '0.92rem' }}>{it.name}</div>
                            <div style={{ fontSize: '0.74rem', color: '#94a3b8' }}>
                              {it.category} {it.sku && `• SKU: ${it.sku}`}
                            </div>
                          </td>
                          <td>
                            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '5px', fontSize: '0.8rem', color: '#475569', fontWeight: 600 }}>
                              <MapPin size={13} color="#6366f1" />
                              {it.dominantCity || activeGroup?.geoName}
                            </span>
                          </td>
                          <td style={{ textAlign: 'center', fontWeight: 700, fontSize: '0.9rem' }}>
                            <AnimatedCounter value={it.unitsSold} duration={1200} />
                          </td>
                          <td style={{ fontWeight: 800, fontSize: '0.94rem', color: '#0f172a' }}>
                            ₹<AnimatedCounter value={it.revenue} duration={1400} />
                          </td>
                          <td>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                              <div style={{ flex: 1, height: '6px', borderRadius: '9999px', background: '#f1f5f9', overflow: 'hidden' }}>
                                <div
                                  style={{
                                    width: `${Math.min(it.marketSharePercentage, 100)}%`,
                                    height: '100%',
                                    borderRadius: '9999px',
                                    background: 'linear-gradient(90deg, #4f46e5, #06b6d4)',
                                  }}
                                />
                              </div>
                              <span style={{ fontSize: '0.76rem', fontWeight: 700, minWidth: '36px', textAlign: 'right' }}>
                                {it.marketSharePercentage}%
                              </span>
                            </div>
                          </td>
                          <td style={{ textAlign: 'right' }}>
                            <span
                              style={{
                                display: 'inline-flex',
                                alignItems: 'center',
                                gap: '4px',
                                padding: '3px 8px',
                                borderRadius: '9999px',
                                background: it.growthRate >= 0 ? 'rgba(220, 252, 231, 0.9)' : 'rgba(254, 226, 226, 0.9)',
                                color: it.growthRate >= 0 ? '#15803d' : '#b91c1c',
                                fontSize: '0.74rem',
                                fontWeight: 700,
                              }}
                            >
                              <TrendingUp size={12} />
                              {it.growthRate >= 0 ? `+${it.growthRate}%` : `${it.growthRate}%`}
                            </span>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
