import React, { useMemo } from 'react';
import { useAuth } from '../context/AuthContext';
import RippleEffect from './common/RippleEffect';
import {
  LayoutDashboard,
  ShoppingCart,
  Boxes,
  History,
  Bot,
  Truck,
  FileText,
  LogOut,
  Store,
  Sparkles,
  Trophy
} from 'lucide-react';

export default function Sidebar({ activeTab, setActiveTab, alertCount = 0 }) {
  const { user, logout } = useAuth();

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'pos', label: 'POS Terminal', icon: ShoppingCart },
    { id: 'inventory', label: 'Inventory Catalog', icon: Boxes },
    { id: 'audit', label: 'Stock Audit Log', icon: History },
    { id: 'leaderboard', label: 'Sales Leaderboard', icon: Trophy },
    { id: 'copilot', label: 'AI Retail Copilot', icon: Bot, isAi: true, badge: alertCount > 0 ? alertCount : null },
    { id: 'purchase-orders', label: 'Purchase Orders', icon: Truck },
    { id: 'ocr-scanner', label: 'OCR Bill Scanner', icon: FileText },
  ];

  const activeIndex = useMemo(
    () => navItems.findIndex((item) => item.id === activeTab),
    [activeTab]
  );

  // Each nav-item height ~43px (11px padding top + 11px bottom + ~19px icon + 2px border), gap 7px
  const indicatorOffset = activeIndex >= 0 ? activeIndex * 50 : 0;

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="logo-badge float-gentle">
          <Store size={22} />
        </div>
        <div className="brand-text">
          <h1>ShelfIQ</h1>
          <span>{user?.storeName || 'Retail Hub'}</span>
        </div>
      </div>

      <nav className="sidebar-nav" style={{ position: 'relative' }}>
        {/* Animated sliding active indicator pill */}
        {activeIndex >= 0 && (
          <div
            className="nav-indicator"
            style={{
              transform: `translateY(${indicatorOffset}px)`,
              top: '0px',
            }}
          />
        )}

        {navItems.map((item, index) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <RippleEffect
              key={item.id}
              as="button"
              onClick={() => setActiveTab(item.id)}
              className={`nav-item stagger-item ${isActive ? 'active' : ''}`}
              style={{ '--stagger-index': index }}
              color="rgba(99, 102, 241, 0.2)"
            >
              <Icon size={19} />
              <span>{item.label}</span>
              {item.isAi && (
                <Sparkles size={14} style={{ color: isActive ? '#fff' : '#c084fc', marginLeft: 'auto' }} />
              )}
              {item.badge && (
                <span className="nav-badge">{item.badge}</span>
              )}
            </RippleEffect>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <div className="user-profile-badge">
          <div className="user-info">
            <span className="user-name">{user?.fullName || 'User'}</span>
            <span className="user-role">{user?.roles?.[0]?.replace('ROLE_', '') || 'STAFF'}</span>
          </div>
          <button
            onClick={logout}
            className="logout-btn"
            title="Sign Out"
          >
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </aside>
  );
}
