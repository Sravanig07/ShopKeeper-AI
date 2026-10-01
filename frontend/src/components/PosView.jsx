import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import TiltCard from './common/TiltCard';
import ParticleExplosion from './common/ParticleExplosion';
import RippleEffect from './common/RippleEffect';
import MagneticButton from './common/MagneticButton';
import AnimatedCounter from './common/AnimatedCounter';
import SkeletonLoader from './common/SkeletonLoader';
import {
  Search,
  Plus,
  Minus,
  Trash2,
  CheckCircle,
  Printer,
  CreditCard,
  QrCode,
  Banknote,
  X,
  Sparkles
} from 'lucide-react';

export default function PosView({ showToast }) {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [cart, setCart] = useState([]);
  const [paymentMethod, setPaymentMethod] = useState('UPI');
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [discountAmount, setDiscountAmount] = useState(0);
  const [completedSale, setCompletedSale] = useState(null);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [confettiTrigger, setConfettiTrigger] = useState(0);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const [prodList, catList] = await Promise.all([
        api.products.getAll(),
        api.categories.getAll(),
      ]);
      const list = Array.isArray(prodList) ? prodList : (prodList?.content || []);
      setProducts(list);
      setCategories(Array.isArray(catList) ? catList : (catList?.content || []));
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setInitialLoading(false);
    }
  };

  const addToCart = (product) => {
    const stock = product.currentStock ?? 10;
    if (stock <= 0) {
      showToast(`${product.name} is currently out of stock!`, 'error');
      return;
    }

    setCart((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        if (existing.quantity >= stock) {
          showToast(`Cannot add more. Only ${stock} units available in stock.`, 'warning');
          return prev;
        }
        return prev.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }
      return [...prev, { product, quantity: 1, unitPrice: product.sellingPrice }];
    });
  };

  const updateQuantity = (productId, delta) => {
    setCart((prev) =>
      prev
        .map((item) => {
          if (item.product.id === productId) {
            const newQty = item.quantity + delta;
            const stock = item.product.currentStock ?? 10;
            if (newQty > stock) {
              showToast(`Only ${stock} available in stock.`, 'warning');
              return item;
            }
            return newQty > 0 ? { ...item, quantity: newQty } : null;
          }
          return item;
        })
        .filter(Boolean)
    );
  };

  const removeFromCart = (productId) => {
    setCart((prev) => prev.filter((item) => item.product.id !== productId));
  };

  const subtotal = cart.reduce(
    (sum, item) => sum + item.unitPrice * item.quantity,
    0
  );
  const tax = Math.round((subtotal - discountAmount) * 0.05 * 100) / 100;
  const netAmount = Math.max(0, subtotal - discountAmount + tax);

  const handleCheckout = async () => {
    if (cart.length === 0) {
      showToast('Cart is empty. Add products before checking out.', 'warning');
      return;
    }

    setLoading(true);
    try {
      const checkoutPayload = {
        items: cart.map((item) => ({
          productId: item.product.id,
          quantity: item.quantity,
          unitPrice: item.unitPrice,
          discountAmount: 0,
        })),
        paymentMethod,
        customerName: customerName.trim() || 'Walk-in Customer',
        customerPhone: customerPhone.trim() || '',
        orderDiscount: Number(discountAmount) || 0,
      };

      const sale = await api.sales.checkout(checkoutPayload);
      setCompletedSale(sale);
      setConfettiTrigger((c) => c + 1);
      setCart([]);
      setCustomerName('');
      setCustomerPhone('');
      setDiscountAmount(0);
      showToast(`Sale completed! Invoice ${sale.invoiceNumber}`, 'success');
      loadProducts(); // refresh stock numbers
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const safeProducts = Array.isArray(products) ? products : (products?.content || []);
  const filteredProducts = safeProducts.filter((p) => {
    const matchesCat =
      selectedCategory === 'ALL' ||
      (p.category && p.category.name === selectedCategory);
    const q = searchQuery.toLowerCase();
    const matchesSearch =
      p.name?.toLowerCase().includes(q) ||
      p.sku?.toLowerCase().includes(q) ||
      (p.barcode && p.barcode.includes(q));
    return matchesCat && matchesSearch;
  });

  return (
    <div>
      {/* Checkout Confetti Particle Burst */}
      <ParticleExplosion trigger={confettiTrigger} duration={2600} particleCount={85} />

      <div className="pos-layout">
        {/* Left: Product Catalog */}
        <div className="pos-catalog-panel">
          <div className="stagger-item" style={{ '--stagger-index': 0, display: 'flex', gap: '12px', alignItems: 'center' }}>
            <div className="search-input-box">
              <Search size={18} />
              <input
                type="text"
                placeholder="Scan barcode, or search by SKU / product name..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && filteredProducts.length === 1) {
                    addToCart(filteredProducts[0]);
                    setSearchQuery('');
                  }
                }}
                className="search-input input-glow-focus"
                autoFocus
              />
            </div>
          </div>

          {/* Category Filter Chips */}
          <div className="stagger-item" style={{ '--stagger-index': 1, display: 'flex', gap: '8px', overflowX: 'auto', paddingBottom: '4px' }}>
            <RippleEffect
              as="button"
              onClick={() => setSelectedCategory('ALL')}
              className={`chip ${selectedCategory === 'ALL' ? 'active' : ''}`}
              style={{
                backgroundColor: selectedCategory === 'ALL' ? '#4f46e5' : '#fff',
                color: selectedCategory === 'ALL' ? '#fff' : '#475569',
                cursor: 'pointer',
                border: '1px solid rgba(199,210,254,0.7)',
              }}
            >
              All Categories ({products.length})
            </RippleEffect>
            {categories.map((c) => (
              <RippleEffect
                key={c.id}
                as="button"
                onClick={() => setSelectedCategory(c.name)}
                className="chip"
                style={{
                  backgroundColor: selectedCategory === c.name ? '#4f46e5' : '#fff',
                  color: selectedCategory === c.name ? '#fff' : '#475569',
                  cursor: 'pointer',
                  border: '1px solid rgba(199,210,254,0.7)',
                }}
              >
                {c.name}
              </RippleEffect>
            ))}
          </div>

          {/* Products Grid */}
          {initialLoading ? (
            <SkeletonLoader variant="card" count={4} />
          ) : (
            <div className="product-cards-grid">
              {filteredProducts.length === 0 ? (
                <div
                  className="card apple-liquid-glass stagger-item"
                  style={{
                    '--stagger-index': 2,
                    gridColumn: '1 / -1',
                    textAlign: 'center',
                    padding: '48px 24px',
                    borderRadius: '24px',
                    color: '#64748b',
                  }}
                >
                  <div
                    style={{
                      width: '60px',
                      height: '60px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, #e0e7ff, #ede9fe)',
                      color: '#6366f1',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      margin: '0 auto 16px',
                    }}
                  >
                    <Search size={28} />
                  </div>
                  <h4 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#0f172a', marginBottom: '6px' }}>
                    No Products in Catalog Yet
                  </h4>
                  <p style={{ fontSize: '0.86rem', color: '#64748b', maxWidth: '440px', margin: '0 auto' }}>
                    Your store catalog is clean and ready. Add your first product in <strong>Inventory Catalog</strong> or scan a paper bill in <strong>OCR Scanner</strong> to start billing!
                  </p>
                </div>
              ) : (
                filteredProducts.map((p, pIdx) => {
                  const stock = p.currentStock ?? 10;
                  const isLow = stock <= (p.safetyStock || 10);
                  const isOut = stock <= 0;

                  return (
                    <div
                      key={p.id}
                      className="stagger-item"
                      style={{ '--stagger-index': Math.min(pIdx, 12) }}
                    >
                      <TiltCard maxTilt={9} scale={1.025}>
                        <div
                          className="product-tile"
                          onClick={() => addToCart(p)}
                          style={{ height: '100%', marginBottom: 0 }}
                        >
                          <div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                              <span className="badge badge-muted" style={{ fontSize: '0.68rem', marginBottom: '6px' }}>
                                {p.category?.name || 'General'}
                              </span>
                              <span
                                className={`badge ${isOut ? 'badge-danger' : isLow ? 'badge-warning' : 'badge-success'}`}
                                style={{ fontSize: '0.68rem' }}
                              >
                                {isOut ? '0 left' : `${stock} left`}
                              </span>
                            </div>
                            <h4>{p.name}</h4>
                            <p>SKU: {p.sku}</p>
                          </div>
                          <div className="product-tile-bottom">
                            <span className="product-price">₹{p.sellingPrice}</span>
                            <button
                              type="button"
                              disabled={isOut}
                              className="btn btn-primary btn-sm"
                              style={{ padding: '4px 10px', borderRadius: '9999px' }}
                            >
                              <Plus size={14} /> Add
                            </button>
                          </div>
                        </div>
                      </TiltCard>
                    </div>
                  );
                })
              )}
            </div>
          )}
        </div>

        {/* Right: Active Cart Ticket */}
        <div className="pos-cart-panel stagger-item" style={{ '--stagger-index': 2 }}>
          <div className="cart-header">
            <h3>Current Bill</h3>
            <span className="badge badge-info">{cart.reduce((a, b) => a + b.quantity, 0)} Items</span>
          </div>

          <div className="cart-items-list">
            {cart.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '40px 10px', color: '#94a3b8' }}>
                Cart is empty.<br />Click products on the left or scan barcodes to begin billing.
              </div>
            ) : (
              cart.map((item) => (
                <div key={item.product.id} className="cart-item-row cart-item-enter">
                  <div style={{ flex: 1, paddingRight: '8px' }}>
                    <div className="cart-item-title">{item.product.name}</div>
                    <div className="cart-item-sub">
                      ₹{item.unitPrice} × {item.quantity} = ₹{(item.unitPrice * item.quantity).toFixed(2)}
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <div className="cart-qty-counter">
                      <button
                        onClick={() => updateQuantity(item.product.id, -1)}
                        className="cart-qty-btn"
                      >
                        <Minus size={12} />
                      </button>
                      <span style={{ fontSize: '0.84rem', fontWeight: 700, minWidth: '18px', textAlign: 'center' }}>
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => updateQuantity(item.product.id, 1)}
                        className="cart-qty-btn"
                      >
                        <Plus size={12} />
                      </button>
                    </div>

                    <button
                      onClick={() => removeFromCart(item.product.id)}
                      style={{ background: 'transparent', color: '#94a3b8', padding: '4px' }}
                      title="Remove"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Customer & Discount Inputs */}
          <div style={{ padding: '12px 20px', borderTop: '1px solid #e2e8f0', display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <input
              type="text"
              placeholder="Customer Name (Optional)"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
              className="input-glow-focus"
              style={{ padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1', fontSize: '0.82rem', background: 'rgba(255,255,255,0.9)' }}
            />
            <input
              type="tel"
              placeholder="Customer Phone (for digital receipt)"
              value={customerPhone}
              onChange={(e) => setCustomerPhone(e.target.value)}
              className="input-glow-focus"
              style={{ padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1', fontSize: '0.82rem', background: 'rgba(255,255,255,0.9)' }}
            />
          </div>

          {/* Payment Method Selector */}
          <div style={{ padding: '10px 20px', borderTop: '1px solid #e2e8f0' }}>
            <label style={{ fontSize: '0.78rem', fontWeight: 600, color: '#64748b', display: 'block', marginBottom: '6px' }}>
              Payment Method
            </label>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '6px' }}>
              <button
                type="button"
                onClick={() => setPaymentMethod('UPI')}
                className={`btn btn-sm ${paymentMethod === 'UPI' ? 'btn-primary' : 'btn-outline'}`}
                style={{ padding: '6px 4px', fontSize: '0.78rem', borderRadius: '9999px' }}
              >
                <QrCode size={14} /> UPI
              </button>
              <button
                type="button"
                onClick={() => setPaymentMethod('CASH')}
                className={`btn btn-sm ${paymentMethod === 'CASH' ? 'btn-primary' : 'btn-outline'}`}
                style={{ padding: '6px 4px', fontSize: '0.78rem', borderRadius: '9999px' }}
              >
                <Banknote size={14} /> Cash
              </button>
              <button
                type="button"
                onClick={() => setPaymentMethod('CARD')}
                className={`btn btn-sm ${paymentMethod === 'CARD' ? 'btn-primary' : 'btn-outline'}`}
                style={{ padding: '6px 4px', fontSize: '0.78rem', borderRadius: '9999px' }}
              >
                <CreditCard size={14} /> Card
              </button>
            </div>
          </div>

          {/* Summary Breakdown */}
          <div className="cart-summary-section">
            <div className="summary-row">
              <span>Gross Subtotal</span>
              <span>₹{subtotal.toFixed(2)}</span>
            </div>
            <div className="summary-row">
              <span>GST / Tax (5%)</span>
              <span>₹{tax.toFixed(2)}</span>
            </div>
            <div className="summary-row summary-total">
              <span>Payable Total</span>
              <span style={{ color: '#4f46e5' }}>
                ₹<AnimatedCounter value={netAmount} decimals={2} duration={500} />
              </span>
            </div>

            <MagneticButton
              onClick={handleCheckout}
              disabled={loading || cart.length === 0}
              className="btn btn-success"
              style={{ width: '100%', padding: '12px', marginTop: '12px', fontSize: '0.98rem', borderRadius: '9999px' }}
            >
              {loading ? 'Processing Sale...' : `Charge ₹${netAmount.toFixed(2)}`}
            </MagneticButton>
          </div>
        </div>
      </div>

      {/* Invoice Receipt Modal with 3D Spring Animation */}
      {completedSale && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '440px' }}>
            <div className="modal-header">
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <CheckCircle size={20} color="#10b981" />
                <h3 style={{ fontSize: '1rem' }}>Transaction Successful</h3>
              </div>
              <button onClick={() => setCompletedSale(null)} style={{ background: 'transparent' }}>
                <X size={18} />
              </button>
            </div>

            <div className="modal-body">
              <div className="receipt-paper">
                <div style={{ textAlign: 'center', marginBottom: '12px' }}>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 800 }}>ShelfIQ Retail Hub</h3>
                  <p style={{ fontSize: '0.75rem', color: '#475569' }}>Tax Invoice & Cash Receipt</p>
                  <p style={{ fontSize: '0.72rem', color: '#64748b' }}>{completedSale.invoiceNumber}</p>
                </div>

                <div className="receipt-divider" />

                <div style={{ fontSize: '0.76rem', color: '#334155', lineHeight: 1.5 }}>
                  <div><strong>Customer:</strong> {completedSale.customerName || 'Walk-in'}</div>
                  {completedSale.customerPhone && <div><strong>Phone:</strong> {completedSale.customerPhone}</div>}
                  <div><strong>Cashier:</strong> {completedSale.cashierName}</div>
                  <div><strong>Payment:</strong> {completedSale.paymentMethod}</div>
                  <div><strong>Time:</strong> {new Date().toLocaleString()}</div>
                </div>

                <div className="receipt-divider" />

                <table style={{ width: '100%', fontSize: '0.78rem', textAlign: 'left', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #cbd5e1' }}>
                      <th style={{ paddingBottom: '4px' }}>Item</th>
                      <th style={{ textAlign: 'center' }}>Qty</th>
                      <th style={{ textAlign: 'right' }}>Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {completedSale.items?.map((it) => (
                      <tr key={it.id}>
                        <td style={{ padding: '4px 0' }}>{it.productName}</td>
                        <td style={{ textAlign: 'center' }}>{it.quantity}</td>
                        <td style={{ textAlign: 'right' }}>₹{it.subtotal}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                <div className="receipt-divider" />

                <div style={{ fontSize: '0.82rem', lineHeight: 1.6 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>Subtotal:</span>
                    <span>₹{completedSale.totalAmount}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>Tax (5%):</span>
                    <span>₹{completedSale.taxAmount}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: '0.95rem', marginTop: '4px' }}>
                    <span>Total Paid:</span>
                    <span>₹{completedSale.netAmount}</span>
                  </div>
                </div>

                <div className="receipt-divider" />
                <div style={{ textAlign: 'center', fontSize: '0.72rem', color: '#64748b', marginTop: '8px' }}>
                  Thank you for shopping with us! Have a great day.
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button
                onClick={() => window.print()}
                className="btn btn-outline"
                style={{ display: 'flex', gap: '6px' }}
              >
                <Printer size={16} /> Print
              </button>
              <MagneticButton
                onClick={() => setCompletedSale(null)}
                className="btn btn-primary"
              >
                Start Next Sale
              </MagneticButton>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
