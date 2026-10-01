import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import {
  Boxes,
  Plus,
  Minus,
  Search,
  History,
  AlertTriangle,
  CheckCircle,
  X,
  PlusCircle,
  FileSpreadsheet
} from 'lucide-react';
import TiltCard from './common/TiltCard';
import AnimatedCounter from './common/AnimatedCounter';
import SkeletonLoader from './common/SkeletonLoader';
import RippleEffect from './common/RippleEffect';
import MagneticButton from './common/MagneticButton';

export default function InventoryView({ showToast, initialSubTab = 'catalog' }) {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [adjustmentDelta, setAdjustmentDelta] = useState(10);
  const [movementType, setMovementType] = useState('RESTOCK');
  const [adjustmentReason, setAdjustmentReason] = useState('');
  const [adjustModalOpen, setAdjustModalOpen] = useState(false);
  const [auditModalOpen, setAuditModalOpen] = useState(false);
  const [auditMovements, setAuditMovements] = useState([]);
  const [addProductModalOpen, setAddProductModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [activeSubTab, setActiveSubTab] = useState(initialSubTab);
  const [auditFilterType, setAuditFilterType] = useState('ALL');

  // New Product Form State
  const [newSku, setNewSku] = useState('');
  const [newBarcode, setNewBarcode] = useState('');
  const [newName, setNewName] = useState('');
  const [newBrand, setNewBrand] = useState('');
  const [newCategoryId, setNewCategoryId] = useState('');
  const [newSupplierId, setNewSupplierId] = useState('');
  const [newSellingPrice, setNewSellingPrice] = useState('');
  const [newCostPrice, setNewCostPrice] = useState('');
  const [newSafetyStock, setNewSafetyStock] = useState('15');
  const [newMinStock, setNewMinStock] = useState('5');
  const [newInitialStock, setNewInitialStock] = useState('20');

  useEffect(() => {
    if (initialSubTab) {
      setActiveSubTab(initialSubTab);
    }
  }, [initialSubTab]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [prodList, catList, supList, moveList] = await Promise.all([
        api.products.getAll(),
        api.categories.getAll(),
        api.suppliers.getAll(),
        api.inventory.getMovements(0, 100).catch(() => []),
      ]);
      const safeProds = Array.isArray(prodList) ? prodList : (prodList?.content || []);
      const safeCats = Array.isArray(catList) ? catList : (catList?.content || []);
      const safeSups = Array.isArray(supList) ? supList : (supList?.content || []);
      const safeMoves = Array.isArray(moveList) ? moveList : (moveList?.content || []);

      setProducts(safeProds);
      setCategories(safeCats);
      setSuppliers(safeSups);
      setAuditMovements(safeMoves);

      if (safeCats?.length > 0 && !newCategoryId) {
        setNewCategoryId(safeCats[0].id);
      }
      if (safeSups?.length > 0 && !newSupplierId) {
        setNewSupplierId(safeSups[0].id);
      }
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setInitialLoading(false);
    }
  };

  const handleOpenAdjust = (product) => {
    setSelectedProduct(product);
    setAdjustmentDelta(10);
    setMovementType('RESTOCK');
    setAdjustmentReason('');
    setAdjustModalOpen(true);
  };

  const handleOpenAuditTrail = async (product) => {
    setSelectedProduct(product);
    setAuditModalOpen(true);
    try {
      if (product) {
        const history = await api.inventory.getProductMovements(product.id);
        setAuditMovements(Array.isArray(history) ? history : (history?.content || []));
      } else {
        const history = await api.inventory.getMovements(0, 100);
        setAuditMovements(Array.isArray(history) ? history : (history?.content || []));
      }
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  const submitStockAdjustment = async (e) => {
    e.preventDefault();
    if (!selectedProduct) return;

    setLoading(true);
    try {
      const delta = parseInt(adjustmentDelta, 10);
      const isNegative = ['DAMAGE', 'SALE'].includes(movementType);
      const finalDelta = isNegative ? -Math.abs(delta) : Math.abs(delta);

      await api.inventory.adjustStock({
        productId: selectedProduct.id,
        quantityDelta: finalDelta,
        movementType,
        reason: adjustmentReason || `Physical Inventory Verification (${movementType})`,
      });

      showToast(`Stock updated for ${selectedProduct.name}`, 'success');
      setAdjustModalOpen(false);
      loadData();
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault();
    if (!newSku || !newName || !newSellingPrice || !newCostPrice) {
      showToast('Please fill all required product fields.', 'warning');
      return;
    }

    setLoading(true);
    try {
      await api.products.create({
        sku: newSku.trim().toUpperCase(),
        barcode: newBarcode.trim() || undefined,
        name: newName.trim(),
        brand: newBrand.trim() || undefined,
        categoryId: newCategoryId ? Number(newCategoryId) : undefined,
        supplierId: newSupplierId ? Number(newSupplierId) : undefined,
        sellingPrice: Number(newSellingPrice),
        costPrice: Number(newCostPrice),
        currentStock: Number(newInitialStock) || 0,
        safetyStock: Number(newSafetyStock) || 10,
        minOrderQuantity: Number(newMinStock) || 5,
        unitOfMeasure: 'PCS',
      });

      showToast(`Product ${newName} created successfully!`, 'success');
      setAddProductModalOpen(false);

      // Reset form
      setNewSku('');
      setNewBarcode('');
      setNewName('');
      setNewBrand('');
      setNewSellingPrice('');
      setNewCostPrice('');
      setNewInitialStock('20');
      loadData();
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const safeProducts = Array.isArray(products) ? products : (products?.content || []);
  const safeAuditMovements = Array.isArray(auditMovements) ? auditMovements : (auditMovements?.content || []);

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

  const filteredAuditMovements = safeAuditMovements.filter((m) => {
    const matchesType = auditFilterType === 'ALL' || m.movementType === auditFilterType;
    const q = searchQuery.toLowerCase();
    const matchesSearch =
      !searchQuery ||
      m.productName?.toLowerCase().includes(q) ||
      m.sku?.toLowerCase().includes(q) ||
      m.reason?.toLowerCase().includes(q);
    return matchesType && matchesSearch;
  });

  const totalAssetValuation = safeProducts.reduce(
    (acc, p) => acc + ((p.currentStock ?? 0) * (p.costPrice || 0)),
    0
  );

  return (
    <div>
      {/* Top Header Controls */}
      <div className="stagger-item" style={{ '--stagger-index': 0, display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '22px' }}>
        <div>
          <h2 style={{ fontSize: '1.45rem', fontWeight: 900, color: '#0f172a', letterSpacing: '-0.02em' }}>
            Catalog & Movement Audit Ledger
          </h2>
          <p style={{ fontSize: '0.84rem', color: '#64748b' }}>
            Live SKU catalog, safety thresholds, and immutable chronological movement audit ledger
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <RippleEffect
            as="button"
            onClick={() => {
              setActiveSubTab(activeSubTab === 'catalog' ? 'audit' : 'catalog');
            }}
            className="btn btn-outline"
            style={{ display: 'flex', gap: '6px' }}
          >
            <History size={16} />
            <span>{activeSubTab === 'audit' ? 'Back to Catalog' : 'View Audit Ledger'}</span>
          </RippleEffect>
          <MagneticButton
            onClick={() => setAddProductModalOpen(true)}
            className="btn btn-primary"
            style={{ display: 'flex', gap: '6px' }}
          >
            <PlusCircle size={16} />
            <span>Add Product</span>
          </MagneticButton>
        </div>
      </div>

      {/* Inventory Health KPIs with Animated Counters */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '22px' }}>
        <div className="stagger-item" style={{ '--stagger-index': 1 }}>
          <TiltCard className="card" maxTilt={7} scale={1.015} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Active SKUs</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#0f172a', marginTop: '4px' }}>
              <AnimatedCounter value={safeProducts.length} duration={1400} />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#0284c7', marginTop: '4px', fontWeight: 600 }}>Catalog items tracked</div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 2 }}>
          <TiltCard className="card" maxTilt={7} scale={1.015} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Low Stock Alert</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#f59e0b', marginTop: '4px' }}>
              <AnimatedCounter
                value={safeProducts.filter(p => (p.currentStock ?? 0) <= (p.safetyStock || 10) && (p.currentStock ?? 0) > 0).length}
                duration={1400}
              />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#d97706', marginTop: '4px', fontWeight: 600 }}>Below safety threshold</div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 3 }}>
          <TiltCard className="card" maxTilt={7} scale={1.015} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Out of Stock</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#ef4444', marginTop: '4px' }}>
              <AnimatedCounter
                value={safeProducts.filter(p => (p.currentStock ?? 0) <= 0).length}
                duration={1400}
              />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#dc2626', marginTop: '4px', fontWeight: 600 }}>Immediate stockout risk</div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 4 }}>
          <TiltCard className="card" maxTilt={7} scale={1.015} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Total Inventory Asset</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#10b981', marginTop: '4px' }}>
              <AnimatedCounter value={totalAssetValuation} prefix="₹" duration={1800} />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#059669', marginTop: '4px', fontWeight: 600 }}>Cost valuation at warehouse</div>
          </TiltCard>
        </div>
      </div>

      {/* Subtabs Bar */}
      <div className="stagger-item" style={{ '--stagger-index': 5, display: 'flex', gap: '8px', marginBottom: '16px' }}>
        <RippleEffect
          as="button"
          onClick={() => setActiveSubTab('catalog')}
          className={`btn ${activeSubTab === 'catalog' ? 'btn-primary' : 'btn-outline'}`}
          style={{ display: 'flex', gap: '8px', alignItems: 'center', fontSize: '0.85rem' }}
        >
          <Boxes size={16} />
          <span>Product Catalog & Stock ({safeProducts.length})</span>
        </RippleEffect>
        <RippleEffect
          as="button"
          onClick={() => {
            setActiveSubTab('audit');
            handleOpenAuditTrail(null);
          }}
          className={`btn ${activeSubTab === 'audit' ? 'btn-primary' : 'btn-outline'}`}
          style={{ display: 'flex', gap: '8px', alignItems: 'center', fontSize: '0.85rem' }}
        >
          <History size={16} />
          <span>Movement Audit Ledger ({safeAuditMovements.length})</span>
        </RippleEffect>
      </div>

      {/* Filter and Search Bar */}
      <div className="card stagger-item" style={{ '--stagger-index': 6, padding: '16px 20px', marginBottom: '18px' }}>
        <div style={{ display: 'flex', gap: '14px', alignItems: 'center', flexWrap: 'wrap' }}>
          <div className="search-input-box" style={{ flex: 1, minWidth: '260px' }}>
            <Search size={18} />
            <input
              type="text"
              placeholder={activeSubTab === 'catalog' ? "Filter by SKU, Barcode, or Product Name..." : "Filter audit log by SKU, Product Name, Reason..."}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="search-input input-glow-focus"
            />
          </div>

          {activeSubTab === 'catalog' ? (
            <div style={{ display: 'flex', gap: '8px', overflowX: 'auto' }}>
              <RippleEffect
                as="button"
                onClick={() => setSelectedCategory('ALL')}
                className="chip"
                style={{
                  backgroundColor: selectedCategory === 'ALL' ? '#4f46e5' : '#f8fafc',
                  color: selectedCategory === 'ALL' ? '#fff' : '#475569',
                  cursor: 'pointer'
                }}
              >
                All ({safeProducts.length})
              </RippleEffect>
              {categories.map((c) => (
                <RippleEffect
                  key={c.id}
                  as="button"
                  onClick={() => setSelectedCategory(c.name)}
                  className="chip"
                  style={{
                    backgroundColor: selectedCategory === c.name ? '#4f46e5' : '#f8fafc',
                    color: selectedCategory === c.name ? '#fff' : '#475569',
                    cursor: 'pointer'
                  }}
                >
                  {c.name}
                </RippleEffect>
              ))}
            </div>
          ) : (
            <div style={{ display: 'flex', gap: '8px', overflowX: 'auto' }}>
              {['ALL', 'RESTOCK', 'SALE', 'ADJUSTMENT', 'DAMAGE', 'RETURN'].map((type) => (
                <RippleEffect
                  key={type}
                  as="button"
                  onClick={() => setAuditFilterType(type)}
                  className="chip"
                  style={{
                    backgroundColor: auditFilterType === type ? '#4f46e5' : '#f8fafc',
                    color: auditFilterType === type ? '#fff' : '#475569',
                    cursor: 'pointer'
                  }}
                >
                  {type}
                </RippleEffect>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Main Table: Either Catalog or Audit */}
      {initialLoading ? (
        <SkeletonLoader variant="table" count={5} />
      ) : activeSubTab === 'catalog' ? (
        <div className="card stagger-item" style={{ '--stagger-index': 7, padding: 0 }}>
          <div className="table-wrapper">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Product / SKU</th>
                  <th>Category</th>
                  <th>Current Stock</th>
                  <th>Safety Stock</th>
                  <th>Unit Cost</th>
                  <th>Selling Price</th>
                  <th>Supplier</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredProducts.length === 0 ? (
                  <tr>
                    <td colSpan="8" style={{ textAlign: 'center', padding: '45px 20px', color: '#64748b' }}>
                      <div style={{ fontWeight: 700, fontSize: '1rem', color: '#1e293b', marginBottom: '6px' }}>
                        Your Inventory Catalog is Clean (0 Products)
                      </div>
                      <div style={{ fontSize: '0.84rem', color: '#64748b' }}>
                        Click <strong>"+ Add Product"</strong> above to add new items or use <strong>"OCR Bill Scanner"</strong> to batch-import inventory from supplier bills.
                      </div>
                    </td>
                  </tr>
                ) : (
                  filteredProducts.map((p, pIdx) => {
                    const stock = p.currentStock ?? 10;
                    const isOut = stock <= 0;
                    const isLow = stock <= (p.safetyStock || 10);

                    return (
                      <tr
                        key={p.id}
                        className="stagger-item"
                        style={{ '--stagger-index': Math.min(pIdx, 15) }}
                      >
                        <td>
                          <div style={{ fontWeight: 700 }}>{p.name}</div>
                          <div style={{ fontSize: '0.74rem', color: '#94a3b8' }}>
                            SKU: {p.sku} | Barcode: {p.barcode || 'N/A'}
                          </div>
                        </td>
                        <td>
                          <span className="badge badge-muted">{p.category?.name || 'General'}</span>
                        </td>
                        <td>
                          <span
                            className={`badge ${isOut ? 'badge-danger' : isLow ? 'badge-warning' : 'badge-success'}`}
                          >
                            {stock} {p.unitOfMeasure || 'PCS'}
                          </span>
                        </td>
                        <td>{p.safetyStock} PCS</td>
                        <td>₹{p.costPrice}</td>
                        <td style={{ fontWeight: 600 }}>₹{p.sellingPrice}</td>
                        <td>{p.supplier?.name || 'Primary Dist.'}</td>
                        <td style={{ textAlign: 'right' }}>
                          <div style={{ display: 'inline-flex', gap: '6px' }}>
                            <button
                              onClick={() => handleOpenAdjust(p)}
                              className="btn btn-outline btn-sm"
                              title="Adjust Stock (+ / -)"
                            >
                              Adjust
                            </button>
                            <button
                              onClick={() => handleOpenAuditTrail(p)}
                              className="btn btn-outline btn-sm"
                              title="View Product History"
                            >
                              <History size={14} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="card stagger-item" style={{ '--stagger-index': 7, padding: 0 }}>
          <div className="table-wrapper">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Timestamp</th>
                  <th>Product</th>
                  <th>Type</th>
                  <th>Delta</th>
                  <th>Stock (Before → After)</th>
                  <th>Reason / Reference</th>
                  <th>Author</th>
                </tr>
              </thead>
              <tbody>
                {filteredAuditMovements.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#94a3b8' }}>
                      No movement audit records found.
                    </td>
                  </tr>
                ) : (
                  filteredAuditMovements.map((m, mIdx) => (
                    <tr
                      key={m.id}
                      className="stagger-item"
                      style={{ '--stagger-index': Math.min(mIdx, 15) }}
                    >
                      <td style={{ fontSize: '0.78rem', color: '#64748b', whiteSpace: 'nowrap' }}>
                        {new Date(m.createdAt).toLocaleString()}
                      </td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{m.productName}</div>
                        <div style={{ fontSize: '0.72rem', color: '#94a3b8' }}>SKU: {m.sku}</div>
                      </td>
                      <td>
                        <span className={`badge ${['RESTOCK', 'RETURN'].includes(m.movementType) ? 'badge-success' : m.movementType === 'SALE' ? 'badge-primary' : 'badge-danger'}`}>
                          {m.movementType}
                        </span>
                      </td>
                      <td style={{ fontWeight: 700, color: m.quantityDelta > 0 ? '#059669' : '#e11d48' }}>
                        {m.quantityDelta > 0 ? `+${m.quantityDelta}` : m.quantityDelta}
                      </td>
                      <td style={{ fontSize: '0.82rem' }}>
                        {m.previousStock} → <strong>{m.newStock}</strong>
                      </td>
                      <td>
                        <div style={{ fontSize: '0.82rem', fontWeight: 500 }}>{m.reason}</div>
                        <div style={{ fontSize: '0.72rem', color: '#94a3b8' }}>Ref: {m.referenceId || 'N/A'}</div>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.82rem', color: '#334155', fontWeight: 500 }}>
                          {m.createdByName || 'System'}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Quick Stock Adjustment Modal */}
      {adjustModalOpen && selectedProduct && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h3>Adjust Stock: {selectedProduct.name}</h3>
              <button onClick={() => setAdjustModalOpen(false)} style={{ background: 'transparent' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={submitStockAdjustment}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div style={{ background: '#f8fafc', padding: '12px 16px', borderRadius: '12px', fontSize: '0.86rem', border: '1px solid #e2e8f0' }}>
                  <span>Current On-Hand Stock: </span>
                  <strong>{selectedProduct.currentStock ?? 10} {selectedProduct.unitOfMeasure || 'PCS'}</strong>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
                    Movement Type
                  </label>
                  <select
                    value={movementType}
                    onChange={(e) => setMovementType(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '10px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  >
                    <option value="RESTOCK">Restock (Received delivery +)</option>
                    <option value="ADJUSTMENT">Manual Inventory Audit Count (+/-)</option>
                    <option value="DAMAGE">Damaged / Expired Stock (-)</option>
                    <option value="RETURN">Customer Return (+)</option>
                  </select>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
                    Quantity Units
                  </label>
                  <input
                    type="number"
                    value={adjustmentDelta}
                    onChange={(e) => setAdjustmentDelta(e.target.value)}
                    required
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '10px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
                    Reason / Audit Note
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Weekly physical verification or Distributor delivery"
                    value={adjustmentReason}
                    onChange={(e) => setAdjustmentReason(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '10px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  onClick={() => setAdjustModalOpen(false)}
                  className="btn btn-outline"
                >
                  Cancel
                </button>
                <MagneticButton
                  type="submit"
                  disabled={loading}
                  className="btn btn-primary"
                >
                  {loading ? 'Saving...' : 'Apply Stock Change'}
                </MagneticButton>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Movement Audit Trail Modal */}
      {auditModalOpen && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '720px' }}>
            <div className="modal-header">
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <History size={20} color="#4f46e5" />
                <h3>Stock Movement Audit Trail</h3>
              </div>
              <button onClick={() => setAuditModalOpen(false)} style={{ background: 'transparent' }}>
                <X size={18} />
              </button>
            </div>

            <div className="modal-body" style={{ maxHeight: '420px', overflowY: 'auto', padding: 0 }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Timestamp</th>
                    <th>Product</th>
                    <th>Type</th>
                    <th>Delta</th>
                    <th>Stock (Before → After)</th>
                    <th>Reason / Author</th>
                  </tr>
                </thead>
                <tbody>
                  {auditMovements.length === 0 ? (
                    <tr>
                      <td colSpan="6" style={{ textAlign: 'center', padding: '30px', color: '#94a3b8' }}>
                        No movement audit records found.
                      </td>
                    </tr>
                  ) : (
                    auditMovements.map((m, mIdx) => (
                      <tr
                        key={m.id}
                        className="stagger-item"
                        style={{ '--stagger-index': Math.min(mIdx, 15) }}
                      >
                        <td style={{ fontSize: '0.78rem', color: '#64748b', whiteSpace: 'nowrap' }}>
                          {new Date(m.createdAt).toLocaleString()}
                        </td>
                        <td>
                          <div style={{ fontWeight: 600 }}>{m.productName}</div>
                          <div style={{ fontSize: '0.72rem', color: '#94a3b8' }}>SKU: {m.sku}</div>
                        </td>
                        <td>
                          <span className={`badge ${m.quantityDelta > 0 ? 'badge-success' : 'badge-danger'}`}>
                            {m.movementType}
                          </span>
                        </td>
                        <td style={{ fontWeight: 700, color: m.quantityDelta > 0 ? '#059669' : '#e11d48' }}>
                          {m.quantityDelta > 0 ? `+${m.quantityDelta}` : m.quantityDelta}
                        </td>
                        <td style={{ fontSize: '0.82rem' }}>
                          {m.previousStock} → <strong>{m.newStock}</strong>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.82rem' }}>{m.reason}</div>
                          <div style={{ fontSize: '0.72rem', color: '#94a3b8' }}>by {m.createdByName || 'System'}</div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            <div className="modal-footer">
              <button onClick={() => setAuditModalOpen(false)} className="btn btn-outline">
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Add Product Modal */}
      {addProductModalOpen && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '580px' }}>
            <div className="modal-header">
              <h3>Add New Product SKU</h3>
              <button onClick={() => setAddProductModalOpen(false)} style={{ background: 'transparent' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreateProduct}>
              <div className="modal-body" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    SKU Code *
                  </label>
                  <input
                    type="text"
                    value={newSku}
                    onChange={(e) => setNewSku(e.target.value)}
                    placeholder="BEV-FANTA-500"
                    required
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Barcode (EAN/UPC)
                  </label>
                  <input
                    type="text"
                    value={newBarcode}
                    onChange={(e) => setNewBarcode(e.target.value)}
                    placeholder="8901234567899"
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div style={{ gridColumn: 'span 2' }}>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Product Name *
                  </label>
                  <input
                    type="text"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                    placeholder="Fanta Orange Flavored Drink 500ml"
                    required
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Category
                  </label>
                  <select
                    value={newCategoryId}
                    onChange={(e) => setNewCategoryId(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  >
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Supplier
                  </label>
                  <select
                    value={newSupplierId}
                    onChange={(e) => setNewSupplierId(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  >
                    {suppliers.map((s) => (
                      <option key={s.id} value={s.id}>{s.name}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Cost Price (₹) *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={newCostPrice}
                    onChange={(e) => setNewCostPrice(e.target.value)}
                    placeholder="32.00"
                    required
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Selling Price (₹) *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={newSellingPrice}
                    onChange={(e) => setNewSellingPrice(e.target.value)}
                    placeholder="40.00"
                    required
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Initial Stock Units
                  </label>
                  <input
                    type="number"
                    value={newInitialStock}
                    onChange={(e) => setNewInitialStock(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>
                    Safety Buffer Stock
                  </label>
                  <input
                    type="number"
                    value={newSafetyStock}
                    onChange={(e) => setNewSafetyStock(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '9px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setAddProductModalOpen(false)} className="btn btn-outline">
                  Cancel
                </button>
                <MagneticButton type="submit" disabled={loading} className="btn btn-primary">
                  {loading ? 'Creating Product...' : 'Save Product SKU'}
                </MagneticButton>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
