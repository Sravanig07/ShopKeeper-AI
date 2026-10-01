import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import {
  Bot,
  Send,
  Sparkles,
  AlertTriangle,
  TrendingDown,
  Truck,
  ArrowRight,
  ShieldAlert,
  DollarSign
} from 'lucide-react';
import TiltCard from './common/TiltCard';
import RippleEffect from './common/RippleEffect';
import MagneticButton from './common/MagneticButton';

export default function AiCopilotView({ onNavigate, showToast }) {
  const [messages, setMessages] = useState([
    {
      sender: 'ai',
      text: `### 👋 Welcome to ShelfIQ Retail Copilot!\n\nI continuously analyze your store's POS velocity, stockouts, and supplier SLAs to keep your shelves profitable.\n\nAsk me anything or select a prompt below to get started!`,
    },
  ]);
  const [inputMessage, setInputMessage] = useState('');
  const [recommendations, setRecommendations] = useState([]);
  const [deadStock, setDeadStock] = useState([]);
  const [activeSideTab, setActiveSideTab] = useState('restock'); // 'restock' or 'deadstock'
  const [loadingChat, setLoadingChat] = useState(false);

  useEffect(() => {
    loadAiData();
  }, []);

  const loadAiData = async () => {
    try {
      const [recs, dead] = await Promise.all([
        api.ai.getRestockRecommendations(),
        api.ai.getDeadStock(),
      ]);
      setRecommendations(recs || []);
      setDeadStock(dead || []);
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const handleSendMessage = async (msgToSend = inputMessage) => {
    const text = msgToSend.trim();
    if (!text) return;

    setMessages((prev) => [...prev, { sender: 'user', text }]);
    setInputMessage('');
    setLoadingChat(true);

    try {
      const response = await api.ai.chat(text);
      setMessages((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: response.reply,
          followups: response.suggestedFollowups,
        },
      ]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: `⚠️ Error fetching AI response: ${err.message}`,
        },
      ]);
    } finally {
      setLoadingChat(false);
    }
  };

  const handleCreatePoFromRec = async (rec) => {
    if (!rec.supplierId) {
      showToast('No supplier associated with this product.', 'warning');
      return;
    }
    try {
      const po = await api.purchaseOrders.create({
        supplierId: rec.supplierId,
        notes: `AI Replenishment Order: ${rec.productName} running low (${rec.currentStock} units left)`,
        items: [
          {
            productId: rec.productId,
            quantity: rec.recommendedOrderQuantity,
            unitCost: rec.unitCost,
          },
        ],
      });
      showToast(`Purchase Order ${po.poNumber} created for ${rec.supplierName}!`, 'success');
      loadAiData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const suggestionChips = [
    'What items need restock?',
    'Show dead stock items',
    'What are today\'s sales?',
    'Which suppliers deliver fastest?',
  ];

  return (
    <div>
      <div className="stagger-item" style={{ '--stagger-index': 0, marginBottom: '20px' }}>
        <h2 style={{ fontSize: '1.45rem', fontWeight: 800, color: '#0f172a', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Sparkles size={24} color="#9333ea" />
          <span>AI Retail Copilot & Decision Engine</span>
        </h2>
        <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
          Heuristic demand forecasting, automated reorder points (ROP), and interactive store advisor
        </p>
      </div>

      <div className="copilot-grid">
        {/* Left: Chat Window */}
        <div className="chat-box stagger-item" style={{ '--stagger-index': 1 }}>
          <div className="chat-messages">
            {messages.map((m, idx) => (
              <div
                key={idx}
                className={`message-bubble ${m.sender === 'user' ? 'bubble-user' : 'bubble-ai'} stagger-item`}
                style={{ '--stagger-index': 0 }}
              >
                <div style={{ whiteSpace: 'pre-line' }}>{m.text}</div>
                {m.followups && m.followups.length > 0 && (
                  <div style={{ marginTop: '12px' }}>
                    <div style={{ fontSize: '0.74rem', fontWeight: 600, color: '#64748b', marginBottom: '6px' }}>
                      Suggested follow-ups:
                    </div>
                    <div className="chips-container">
                      {m.followups.map((chipText, cIdx) => (
                        <RippleEffect
                          key={cIdx}
                          as="button"
                          onClick={() => handleSendMessage(chipText)}
                          className="chip"
                        >
                          {chipText}
                        </RippleEffect>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            ))}
            {loadingChat && (
              <div className="message-bubble bubble-ai breathe-glow" style={{ color: '#64748b' }}>
                <Sparkles size={16} className="spin" style={{ display: 'inline', marginRight: '6px', color: '#9333ea' }} />
                Analyzing store velocity and inventory matrices...
              </div>
            )}
          </div>

          <div style={{ padding: '0 20px 8px' }}>
            <div className="chips-container">
              {suggestionChips.map((chip, idx) => (
                <RippleEffect
                  key={idx}
                  as="button"
                  onClick={() => handleSendMessage(chip)}
                  className="chip"
                >
                  {chip}
                </RippleEffect>
              ))}
            </div>
          </div>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSendMessage();
            }}
            className="chat-input-bar"
          >
            <input
              type="text"
              placeholder="Ask anything (e.g. Which supplier delivers fastest? What should I order?)..."
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              className="chat-input input-glow-focus"
              style={{ borderRadius: '9999px', padding: '12px 20px' }}
            />
            <MagneticButton
              type="submit"
              disabled={loadingChat || !inputMessage.trim()}
              className="btn btn-primary"
              style={{ padding: '10px 22px', borderRadius: '9999px' }}
            >
              <Send size={18} />
            </MagneticButton>
          </form>
        </div>

        {/* Right: Recommendations & Dead Stock Panel */}
        <div className="stagger-item" style={{ '--stagger-index': 2, display: 'flex', flexDirection: 'column', gap: '14px', overflowY: 'auto' }}>
          <div style={{ display: 'flex', background: 'rgba(226,232,240,0.85)', backdropFilter: 'blur(10px)', borderRadius: '9999px', padding: '4px' }}>
            <RippleEffect
              as="button"
              onClick={() => setActiveSideTab('restock')}
              style={{
                flex: 1,
                padding: '8px',
                borderRadius: '9999px',
                fontSize: '0.82rem',
                fontWeight: 600,
                background: activeSideTab === 'restock' ? '#fff' : 'transparent',
                color: activeSideTab === 'restock' ? '#4f46e5' : '#64748b',
                boxShadow: activeSideTab === 'restock' ? '0 2px 6px rgba(0,0,0,0.08)' : 'none',
              }}
            >
              Restock Alerts ({recommendations.filter((r) => r.stockoutRiskLevel !== 'OPTIMAL').length})
            </RippleEffect>
            <RippleEffect
              as="button"
              onClick={() => setActiveSideTab('deadstock')}
              style={{
                flex: 1,
                padding: '8px',
                borderRadius: '9999px',
                fontSize: '0.82rem',
                fontWeight: 600,
                background: activeSideTab === 'deadstock' ? '#fff' : 'transparent',
                color: activeSideTab === 'deadstock' ? '#4f46e5' : '#64748b',
                boxShadow: activeSideTab === 'deadstock' ? '0 2px 6px rgba(0,0,0,0.08)' : 'none',
              }}
            >
              Dead Stock ({deadStock.length})
            </RippleEffect>
          </div>

          {activeSideTab === 'restock' ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {recommendations.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '30px 14px', color: '#94a3b8' }}>
                  All inventory healthy. No restock alerts needed right now.
                </div>
              ) : (
                recommendations.map((rec, rIdx) => {
                  const isCrit = rec.stockoutRiskLevel === 'CRITICAL';
                  const isWarn = rec.stockoutRiskLevel === 'WARNING';

                  return (
                    <div key={rec.productId} className="stagger-item" style={{ '--stagger-index': rIdx }}>
                      <TiltCard
                        className="card glass-hover-card"
                        maxTilt={7}
                        scale={1.015}
                        style={{
                          padding: '16px',
                          marginBottom: 0,
                          borderLeft: `4px solid ${isCrit ? '#ef4444' : isWarn ? '#f59e0b' : '#10b981'}`,
                          background: 'rgba(255, 255, 255, 0.88)',
                        }}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                          <div>
                            <div style={{ fontWeight: 700, fontSize: '0.92rem' }}>{rec.productName}</div>
                            <div style={{ fontSize: '0.74rem', color: '#94a3b8' }}>{rec.categoryName}</div>
                          </div>
                          <span className={`badge ${isCrit ? 'badge-danger' : isWarn ? 'badge-warning' : 'badge-success'}`}>
                            {rec.stockoutRiskLevel}
                          </span>
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginTop: '10px', fontSize: '0.78rem', color: '#475569' }}>
                          <div>Current Stock: <strong>{rec.currentStock} units</strong></div>
                          <div>Velocity: <strong>{rec.dailySalesVelocity} / day</strong></div>
                          <div>Depletion: <strong>~{rec.daysOfInventoryRemaining} days</strong></div>
                          <div>Supplier: <strong>{rec.supplierName}</strong></div>
                        </div>

                        <div style={{ marginTop: '12px', paddingTop: '10px', borderTop: '1px solid #f1f5f9', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                          <div style={{ fontSize: '0.8rem' }}>
                            Reorder: <strong>{rec.recommendedOrderQuantity} units</strong> (~₹{rec.estimatedRestockCost})
                          </div>
                          <RippleEffect
                            as="button"
                            onClick={() => handleCreatePoFromRec(rec)}
                            className="btn btn-primary btn-sm"
                            style={{ padding: '5px 12px', fontSize: '0.75rem', borderRadius: '9999px' }}
                          >
                            <Truck size={13} /> Order
                          </RippleEffect>
                        </div>
                      </TiltCard>
                    </div>
                  );
                })
              )}
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {deadStock.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '30px 14px', color: '#94a3b8' }}>
                  No dead stock detected! High turnover across all product lines.
                </div>
              ) : (
                deadStock.map((ds, dsIdx) => (
                  <div key={ds.productId} className="stagger-item" style={{ '--stagger-index': dsIdx }}>
                    <TiltCard
                      className="card glass-hover-card"
                      maxTilt={7}
                      scale={1.015}
                      style={{ padding: '16px', marginBottom: 0, background: 'rgba(255, 255, 255, 0.88)' }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                          <div style={{ fontWeight: 700, fontSize: '0.92rem' }}>{ds.productName}</div>
                          <div style={{ fontSize: '0.74rem', color: '#94a3b8' }}>{ds.categoryName}</div>
                        </div>
                        <span className="badge badge-warning">
                          {ds.daysSinceLastSale} days idle
                        </span>
                      </div>

                      <div style={{ marginTop: '8px', fontSize: '0.82rem', color: '#334155' }}>
                        Locked Working Capital: <strong style={{ color: '#e11d48' }}>₹{ds.lockedCapital}</strong> ({ds.currentStock} units)
                      </div>

                      <div style={{ marginTop: '8px', background: '#f8fafc', padding: '8px 10px', borderRadius: '6px', fontSize: '0.76rem', color: '#64748b' }}>
                        💡 {ds.suggestedAction}
                      </div>
                    </TiltCard>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
