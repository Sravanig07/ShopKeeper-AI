import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import {
  Truck,
  Plus,
  CheckCircle,
  Clock,
  Building2,
  PackageCheck,
  X,
  PlusCircle
} from 'lucide-react';
import TiltCard from './common/TiltCard';
import AnimatedCounter from './common/AnimatedCounter';
import SkeletonLoader from './common/SkeletonLoader';
import RippleEffect from './common/RippleEffect';
import MagneticButton from './common/MagneticButton';

export default function PurchaseOrdersView({ showToast }) {
  const [purchaseOrders, setPurchaseOrders] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [products, setProducts] = useState([]);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [selectedSupplierId, setSelectedSupplierId] = useState('');
  const [poNotes, setPoNotes] = useState('');
  const [orderItems, setOrderItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [poPaged, supList, prodList] = await Promise.all([
        api.purchaseOrders.getAll(0, 50),
        api.suppliers.getAll(),
        api.products.getAll(),
      ]);
      setPurchaseOrders(Array.isArray(poPaged) ? poPaged : (poPaged?.content || []));
      setSuppliers(Array.isArray(supList) ? supList : (supList?.content || []));
      const list = Array.isArray(prodList) ? prodList : (prodList?.content || []);
      setProducts(list);
      if (supList?.length > 0 && !selectedSupplierId) {
        setSelectedSupplierId(supList[0].id);
      }
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setInitialLoading(false);
    }
  };

  const handleReceivePo = async (poId) => {
    setLoading(true);
    try {
      const received = await api.purchaseOrders.receive(poId);
      showToast(`Purchase order ${received.poNumber} received! Inventory has been automatically restocked.`, 'success');
      loadData();
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleAddOrderItem = () => {
    if (products.length === 0) return;
    const defaultProd = products[0];
    setOrderItems((prev) => [
      ...prev,
      {
        productId: defaultProd.id,
        quantity: 20,
        unitCost: defaultProd.costPrice || 30.0,
      },
    ]);
  };

  const handleUpdateItem = (index, field, value) => {
    setOrderItems((prev) =>
      prev.map((it, idx) => {
        if (idx !== index) return it;
        if (field === 'productId') {
          const selected = products.find((p) => p.id === Number(value));
          return {
            ...it,
            productId: Number(value),
            unitCost: selected?.costPrice || it.unitCost,
          };
        }
        return { ...it, [field]: value };
      })
    );
  };

  const handleRemoveOrderItem = (index) => {
    setOrderItems((prev) => prev.filter((_, idx) => idx !== index));
  };

  const handleCreatePoSubmit = async (e) => {
    e.preventDefault();
    if (orderItems.length === 0) {
      showToast('Please add at least one product item to the order.', 'warning');
      return;
    }

    setLoading(true);
    try {
      await api.purchaseOrders.create({
        supplierId: Number(selectedSupplierId),
        notes: poNotes,
        expectedDeliveryDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        items: orderItems.map((it) => ({
          productId: it.productId,
          quantity: Number(it.quantity),
          unitCost: Number(it.unitCost),
        })),
      });

      showToast('Purchase Order created and issued to distributor!', 'success');
      setCreateModalOpen(false);
      setOrderItems([]);
      setPoNotes('');
      loadData();
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  };

  const totalSupplierSpend = purchaseOrders.reduce(
    (sum, po) => sum + (Number(po.totalCost) || 0),
    0
  );

  return (
    <div>
      {/* Top Header */}
      <div className="stagger-item" style={{ '--stagger-index': 0, display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '22px' }}>
        <div>
          <h2 style={{ fontSize: '1.45rem', fontWeight: 800, color: '#0f172a' }}>Purchase Orders & Suppliers</h2>
          <p style={{ fontSize: '0.85rem', color: '#64748b' }}>Replenishment supply chain, distributor SLAs, and automated receiving</p>
        </div>
        <MagneticButton
          onClick={() => {
            handleAddOrderItem();
            setCreateModalOpen(true);
          }}
          className="btn btn-primary"
          style={{ display: 'flex', gap: '6px' }}
        >
          <PlusCircle size={16} />
          <span>Create Purchase Order</span>
        </MagneticButton>
      </div>

      {/* PO Overview KPIs */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '22px' }}>
        <div className="stagger-item" style={{ '--stagger-index': 1 }}>
          <TiltCard className="card" maxTilt={8} scale={1.02} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Total Purchase Orders</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#0f172a', marginTop: '4px' }}>
              <AnimatedCounter value={purchaseOrders.length} duration={1200} />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#0284c7', marginTop: '4px', fontWeight: 600 }}>Active PO stream</div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 2 }}>
          <TiltCard className="card" maxTilt={8} scale={1.02} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Pending Receiving</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#f59e0b', marginTop: '4px' }}>
              <AnimatedCounter
                value={purchaseOrders.filter((po) => po.status !== 'RECEIVED').length}
                duration={1200}
              />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#d97706', marginTop: '4px', fontWeight: 600 }}>Awaiting supplier delivery</div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 3 }}>
          <TiltCard className="card" maxTilt={8} scale={1.02} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Received Restocks</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#10b981', marginTop: '4px' }}>
              <AnimatedCounter
                value={purchaseOrders.filter((po) => po.status === 'RECEIVED').length}
                duration={1200}
              />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#059669', marginTop: '4px', fontWeight: 600 }}>Completed into inventory</div>
          </TiltCard>
        </div>

        <div className="stagger-item" style={{ '--stagger-index': 4 }}>
          <TiltCard className="card" maxTilt={8} scale={1.02} style={{ padding: '16px 20px', marginBottom: 0 }}>
            <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Total Supplier Spend</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#6366f1', marginTop: '4px' }}>
              <AnimatedCounter value={totalSupplierSpend} prefix="₹" duration={1800} />
            </div>
            <div style={{ fontSize: '0.74rem', color: '#4f46e5', marginTop: '4px', fontWeight: 600 }}>Lifetime purchase volume</div>
          </TiltCard>
        </div>
      </div>

      {/* Suppliers Cards */}
      <div className="stagger-item" style={{ '--stagger-index': 5, marginBottom: '12px' }}>
        <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#1e293b', marginBottom: '12px' }}>
          Distributor Network & Reliability SLA ({suppliers.length})
        </h3>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        {suppliers.map((s, sIdx) => (
          <div key={s.id} className="stagger-item" style={{ '--stagger-index': 6 + sIdx }}>
            <TiltCard className="card glass-hover-card" maxTilt={8} scale={1.02} style={{ padding: '18px', marginBottom: 0, background: 'rgba(255, 255, 255, 0.88)' }}>
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                  <div style={{ width: '40px', height: '40px', borderRadius: '12px', background: 'linear-gradient(135deg, #e0e7ff, #ede9fe)', color: '#4f46e5', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 2px 8px rgba(79, 70, 229, 0.15)' }}>
                    <Building2 size={20} />
                  </div>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '0.95rem', color: '#0f172a' }}>{s.name}</div>
                    <div style={{ fontSize: '0.76rem', color: '#64748b' }}>Contact: {s.contactPerson}</div>
                  </div>
                </div>
                <span className="badge badge-success">
                  {(s.reliabilityScore * 100).toFixed(0)}% SLA
                </span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginTop: '14px', fontSize: '0.8rem', color: '#475569' }}>
                <div>Lead Time: <strong>{s.leadTimeDays || 2} Days</strong></div>
                <div>Phone: <strong>{s.phone}</strong></div>
              </div>
            </TiltCard>
          </div>
        ))}
      </div>

      {/* Orders Table */}
      {initialLoading ? (
        <SkeletonLoader variant="table" count={4} />
      ) : (
        <div className="card stagger-item" style={{ '--stagger-index': 8, padding: 0 }}>
          <div className="card-header" style={{ padding: '16px 20px', borderBottom: '1px solid #e2e8f0', margin: 0 }}>
            <h3>Recent Purchase Orders ({purchaseOrders.length})</h3>
          </div>

          <div className="table-wrapper" style={{ border: 'none' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>PO Number</th>
                  <th>Supplier</th>
                  <th>Items Ordered</th>
                  <th>Total Cost</th>
                  <th>Expected Delivery</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {purchaseOrders.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: '#94a3b8' }}>
                      No purchase orders placed yet.
                    </td>
                  </tr>
                ) : (
                  purchaseOrders.map((po, poIdx) => {
                    const isReceived = po.status === 'RECEIVED';
                    return (
                      <tr key={po.id} className="stagger-item" style={{ '--stagger-index': Math.min(poIdx, 15) }}>
                        <td>
                          <div style={{ fontWeight: 700 }}>{po.poNumber}</div>
                          <div style={{ fontSize: '0.74rem', color: '#94a3b8' }}>
                            {new Date(po.createdAt).toLocaleDateString()}
                          </div>
                        </td>
                        <td>
                          <div style={{ fontWeight: 600 }}>{po.supplierName}</div>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.82rem' }}>
                            {po.items?.map((it) => `${it.productName} (${it.quantity} units)`).join(', ') || 'N/A'}
                          </div>
                        </td>
                        <td style={{ fontWeight: 700 }}>₹{po.totalCost}</td>
                        <td>
                          <div style={{ fontSize: '0.82rem', color: '#475569' }}>
                            {po.expectedDeliveryDate || 'Standard 2-Day'}
                          </div>
                        </td>
                        <td>
                          <span className={`badge ${isReceived ? 'badge-success' : 'badge-warning'}`}>
                            {isReceived ? (
                              <>
                                <CheckCircle size={12} /> Received
                              </>
                            ) : (
                              <>
                                <Clock size={12} /> Ordered
                              </>
                            )}
                          </span>
                        </td>
                        <td style={{ textAlign: 'right' }}>
                          {isReceived ? (
                            <span style={{ fontSize: '0.78rem', color: '#059669', fontWeight: 600 }}>
                              Restocked on {po.receivedAt ? new Date(po.receivedAt).toLocaleDateString() : 'Today'}
                            </span>
                          ) : (
                            <RippleEffect
                              as="button"
                              onClick={() => handleReceivePo(po.id)}
                              disabled={loading}
                              className="btn btn-success btn-sm"
                              style={{ display: 'inline-flex', gap: '5px' }}
                            >
                              <PackageCheck size={14} /> Receive & Restock
                            </RippleEffect>
                          )}
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Create PO Modal */}
      {createModalOpen && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '640px' }}>
            <div className="modal-header">
              <h3>Create New Purchase Order</h3>
              <button onClick={() => setCreateModalOpen(false)} style={{ background: 'transparent' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreatePoSubmit}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
                    Select Distributor / Supplier *
                  </label>
                  <select
                    value={selectedSupplierId}
                    onChange={(e) => setSelectedSupplierId(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '10px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  >
                    {suppliers.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.name} (Lead time: {s.leadTimeDays || 2} days, {((s.reliabilityScore || 0.95) * 100).toFixed(0)}% SLA)
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <label style={{ fontSize: '0.82rem', fontWeight: 600, color: '#334155' }}>
                      Order Line Items
                    </label>
                    <button
                      type="button"
                      onClick={handleAddOrderItem}
                      className="btn btn-outline btn-sm"
                      style={{ padding: '4px 10px', fontSize: '0.76rem' }}
                    >
                      <Plus size={12} /> Add Item
                    </button>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', maxHeight: '200px', overflowY: 'auto' }}>
                    {orderItems.map((item, idx) => (
                      <div key={idx} style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                        <select
                          value={item.productId}
                          onChange={(e) => handleUpdateItem(idx, 'productId', e.target.value)}
                          className="input-glow-focus"
                          style={{ flex: 2, padding: '8px 12px', borderRadius: '9999px', border: '1px solid #cbd5e1', fontSize: '0.82rem' }}
                        >
                          {products.map((p) => (
                            <option key={p.id} value={p.id}>
                              {p.name} ({p.sku})
                            </option>
                          ))}
                        </select>
                        <input
                          type="number"
                          min="1"
                          placeholder="Qty"
                          value={item.quantity}
                          onChange={(e) => handleUpdateItem(idx, 'quantity', e.target.value)}
                          className="input-glow-focus"
                          style={{ width: '80px', padding: '8px 12px', borderRadius: '9999px', border: '1px solid #cbd5e1', fontSize: '0.82rem' }}
                        />
                        <input
                          type="number"
                          step="0.01"
                          placeholder="Cost"
                          value={item.unitCost}
                          onChange={(e) => handleUpdateItem(idx, 'unitCost', e.target.value)}
                          className="input-glow-focus"
                          style={{ width: '90px', padding: '8px 12px', borderRadius: '9999px', border: '1px solid #cbd5e1', fontSize: '0.82rem' }}
                        />
                        <button
                          type="button"
                          onClick={() => handleRemoveOrderItem(idx)}
                          style={{ background: 'transparent', color: '#ef4444', padding: '6px' }}
                        >
                          <X size={16} />
                        </button>
                      </div>
                    ))}
                  </div>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.82rem', fontWeight: 600, color: '#334155', marginBottom: '6px' }}>
                    Order Notes / Delivery Instructions
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Urgent morning delivery before weekend rush"
                    value={poNotes}
                    onChange={(e) => setPoNotes(e.target.value)}
                    className="input-glow-focus"
                    style={{ width: '100%', padding: '10px 14px', borderRadius: '9999px', border: '1px solid #cbd5e1' }}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setCreateModalOpen(false)} className="btn btn-outline">
                  Cancel
                </button>
                <MagneticButton type="submit" disabled={loading} className="btn btn-primary">
                  {loading ? 'Submitting PO...' : 'Issue Purchase Order'}
                </MagneticButton>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
