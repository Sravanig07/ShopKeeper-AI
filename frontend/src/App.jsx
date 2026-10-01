import React, { useState, useCallback, useRef } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginView from './components/LoginView';
import Sidebar from './components/Sidebar';
import DashboardView from './components/DashboardView';
import PosView from './components/PosView';
import InventoryView from './components/InventoryView';
import AiCopilotView from './components/AiCopilotView';
import PurchaseOrdersView from './components/PurchaseOrdersView';
import OcrScannerView from './components/OcrScannerView';
import LeaderboardView from './components/LeaderboardView';
import AmbientGlow from './components/common/AmbientGlow';
import PageTransition from './components/common/PageTransition';
import { Store, User, CheckCircle, AlertCircle, Info } from 'lucide-react';

function MainApp() {
  const { isAuthenticated, user } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [toasts, setToasts] = useState([]);
  const toastIdRef = useRef(0);

  const showToast = useCallback((message, type = 'info') => {
    toastIdRef.current += 1;
    const id = toastIdRef.current;
    setToasts((prev) => [...prev, { id, message, type, phase: 'enter' }]);
    
    // Start exit animation after 3.5s
    setTimeout(() => {
      setToasts((prev) =>
        prev.map((t) => (t.id === id ? { ...t, phase: 'exit' } : t))
      );
    }, 3500);
    
    // Remove from DOM after exit animation completes
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  }, []);

  if (!isAuthenticated) {
    return <LoginView />;
  }

  const getPageTitle = () => {
    switch (activeTab) {
      case 'dashboard':
        return { title: 'Store Overview', subtitle: 'Retail health KPIs, velocity metrics & alerts' };
      case 'pos':
        return { title: 'POS Checkout Terminal', subtitle: 'Fast barcode cashier checkout & receipt printing' };
      case 'inventory':
        return { title: 'Product Catalog & Inventory', subtitle: 'Master product catalog, stock levels & physical adjustments' };
      case 'audit':
        return { title: 'Stock Movement Audit Ledger', subtitle: 'Immutable chronological audit trail of all store inventory changes' };
      case 'leaderboard':
        return { title: 'Cross-Territory Sales Leaderboard', subtitle: 'Top selling items and categories across cities, states, and macro regions' };
      case 'copilot':
        return { title: 'AI Retail Copilot', subtitle: 'Intelligent replenishment recommendations & store advisor' };
      case 'purchase-orders':
        return { title: 'Purchase Orders', subtitle: 'Supplier replenishment & one-click receiving' };
      case 'ocr-scanner':
        return { title: 'OCR Bill Scanner', subtitle: 'Digitize paper distributor invoices and auto-restock' };
      default:
        return { title: 'ShelfIQ', subtitle: 'Retail Intelligence Platform' };
    }
  };

  const { title, subtitle } = getPageTitle();

  const renderActiveView = () => {
    switch (activeTab) {
      case 'dashboard':
        return <DashboardView onNavigate={setActiveTab} showToast={showToast} />;
      case 'pos':
        return <PosView showToast={showToast} />;
      case 'inventory':
        return <InventoryView showToast={showToast} initialSubTab="catalog" />;
      case 'audit':
        return <InventoryView showToast={showToast} initialSubTab="audit" />;
      case 'leaderboard':
        return <LeaderboardView showToast={showToast} />;
      case 'copilot':
        return <AiCopilotView onNavigate={setActiveTab} showToast={showToast} />;
      case 'purchase-orders':
        return <PurchaseOrdersView showToast={showToast} />;
      case 'ocr-scanner':
        return <OcrScannerView onNavigate={setActiveTab} showToast={showToast} />;
      default:
        return null;
    }
  };

  return (
    <div className="app-container">
      <AmbientGlow />
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      <div className="main-wrapper">
        <header className="top-header">
          <div className="header-title-block">
            <h2>{title}</h2>
            <p>{subtitle}</p>
          </div>

          <div className="header-actions">
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: 'rgba(241,245,249,0.85)', backdropFilter: 'blur(12px)', padding: '6px 12px', borderRadius: '9999px', fontSize: '0.82rem', color: '#334155', border: '1px solid rgba(226,232,240,0.8)', boxShadow: 'inset 0 1px 1px rgba(255,255,255,0.9)' }}>
              <Store size={15} color="#4f46e5" />
              <strong>{user?.storeName || 'Store #1'}</strong>
              <span style={{ color: '#94a3b8' }}>•</span>
              <span style={{ color: '#64748b' }}>{user?.storeCode || 'ST-METRO01'}</span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: 'linear-gradient(135deg, rgba(238,242,255,0.9), rgba(224,231,255,0.9))', backdropFilter: 'blur(12px)', padding: '6px 12px', borderRadius: '9999px', fontSize: '0.82rem', color: '#4338ca', fontWeight: 600, border: '1px solid rgba(199,210,254,0.8)', boxShadow: 'inset 0 1px 1px rgba(255,255,255,0.9)' }}>
              <User size={15} />
              <span>{user?.fullName || 'Vikram Sharma'}</span>
            </div>
          </div>
        </header>

        <main className="content-body">
          <PageTransition activeKey={activeTab}>
            {renderActiveView()}
          </PageTransition>
        </main>
      </div>

      {/* Spring-Animated Toast Notification Container */}
      <div className="toast-container">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={`toast toast-${t.type} ${t.phase === 'exit' ? 'toast-spring-exit' : 'toast-spring-enter'}`}
          >
            {t.type === 'success' && <CheckCircle size={18} />}
            {t.type === 'error' && <AlertCircle size={18} />}
            {t.type === 'warning' && <AlertCircle size={18} />}
            {t.type === 'info' && <Info size={18} />}
            <span>{t.message}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}
