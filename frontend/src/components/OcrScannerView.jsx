import React, { useState } from 'react';
import { api } from '../api/client';
import {
  FileText,
  UploadCloud,
  CheckCircle,
  AlertCircle,
  ArrowRight,
  PackagePlus,
  RefreshCw,
  Sparkles
} from 'lucide-react';
import TiltCard from './common/TiltCard';
import RippleEffect from './common/RippleEffect';
import MagneticButton from './common/MagneticButton';
import AnimatedCounter from './common/AnimatedCounter';

export default function OcrScannerView({ onNavigate, showToast }) {
  const [file, setFile] = useState(null);
  const [parsedInvoice, setParsedInvoice] = useState(null);
  const [scanning, setScanning] = useState(false);
  const [restocking, setRestocking] = useState(false);

  const handleFileUpload = async (uploadedFile) => {
    if (!uploadedFile) return;
    setFile(uploadedFile);
    setScanning(true);
    try {
      const result = await api.ocr.scanInvoice(uploadedFile);
      setParsedInvoice(result);
      showToast('Invoice scanned and line items extracted successfully!', 'success');
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setScanning(false);
    }
  };

  const handleLoadSampleInvoice = async () => {
    // Generate sample invoice blob for instant demo testing
    const sampleText = "BEVERAGE WORLD DISTRIBUTORS INVOICE #BWD-2026-991\nCoca-Cola 500ml Pet Bottle x 24 @ 32.00\nSprite Lime 500ml x 12 @ 31.50\nRed Bull Energy Drink 250ml x 12 @ 98.00";
    const sampleBlob = new Blob([sampleText], { type: 'text/plain' });
    const sampleFile = new File([sampleBlob], 'beverage-distributor-invoice.png', { type: 'image/png' });
    handleFileUpload(sampleFile);
  };

  const handleQuantityChange = (index, newQty) => {
    if (!parsedInvoice) return;
    const updatedItems = parsedInvoice.items.map((it, idx) => {
      if (idx !== index) return it;
      const q = Math.max(1, Number(newQty) || 1);
      return {
        ...it,
        quantity: q,
        lineTotal: q * it.unitCost,
      };
    });

    const newTotal = updatedItems.reduce((sum, it) => sum + it.lineTotal, 0);
    setParsedInvoice({
      ...parsedInvoice,
      totalAmount: newTotal,
      items: updatedItems,
    });
  };

  const handleConfirmRestock = async () => {
    if (!parsedInvoice || !parsedInvoice.items?.length) return;

    setRestocking(true);
    try {
      const payload = {
        supplierId: parsedInvoice.matchedSupplierId,
        invoiceNumber: parsedInvoice.invoiceNumber,
        items: parsedInvoice.items.map((it) => ({
          productId: it.matchedProductId,
          quantity: it.quantity,
          unitCost: it.unitCost,
        })),
      };

      const result = await api.ocr.confirmRestock(payload);
      showToast(result.message || 'Inventory successfully restocked!', 'success');
      setParsedInvoice(null);
      setFile(null);
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setRestocking(false);
    }
  };

  return (
    <div>
      <div className="stagger-item" style={{ '--stagger-index': 0, marginBottom: '24px' }}>
        <h2 style={{ fontSize: '1.45rem', fontWeight: 800, color: '#0f172a', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <FileText size={24} color="#0284c7" />
          <span>Smart OCR Invoice & Bill Scanner</span>
        </h2>
        <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
          Upload supplier paper bills or delivery receipts to automatically extract items, verify catalog matches, and restock inventory
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: parsedInvoice ? '1fr 1.6fr' : '1fr', gap: '24px' }}>
        {/* Upload Dropzone */}
        <div className="stagger-item" style={{ '--stagger-index': 1 }}>
          <TiltCard className="card" maxTilt={5} scale={1.01} style={{ background: 'rgba(255, 255, 255, 0.88)' }}>
            <div className="card-header">
              <h3>Upload Paper Invoice / Bill</h3>
              <RippleEffect
                as="button"
                onClick={handleLoadSampleInvoice}
                disabled={scanning}
                className="btn btn-outline btn-sm"
                style={{ display: 'flex', gap: '6px' }}
              >
                <Sparkles size={14} color="#9333ea" />
                <span>Load Sample Bill</span>
              </RippleEffect>
            </div>

            <label className="ocr-dropzone" style={{ display: 'block', position: 'relative', overflow: 'hidden' }}>
              {scanning && <div className="ocr-laser" />}
              <input
                type="file"
                accept="image/*,.pdf"
                style={{ display: 'none' }}
                onChange={(e) => {
                  if (e.target.files?.[0]) {
                    handleFileUpload(e.target.files[0]);
                  }
                }}
              />
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <div style={{ width: '56px', height: '56px', borderRadius: '50%', background: 'linear-gradient(135deg, #e0e7ff, #ede9fe)', color: '#0284c7', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px', boxShadow: '0 4px 14px rgba(2, 132, 199, 0.2)' }}>
                  <UploadCloud size={28} />
                </div>
                <div style={{ fontWeight: 700, fontSize: '1rem', color: '#1e293b' }}>
                  {file ? file.name : 'Click or Drag Distributor Invoice Here'}
                </div>
                <p>Supports camera photos, PNG, JPG, or PDF delivery challans</p>
                <div style={{ marginTop: '16px' }}>
                  <span className="btn btn-primary btn-sm">
                    {scanning ? 'Extracting text with OCR...' : 'Select File from Device'}
                  </span>
                </div>
              </div>
            </label>

            <div style={{ marginTop: '20px', background: 'rgba(248, 250, 252, 0.85)', backdropFilter: 'blur(8px)', padding: '14px', borderRadius: '14px', border: '1px solid rgba(226, 232, 240, 0.8)', fontSize: '0.82rem', color: '#475569', lineHeight: 1.5 }}>
              <div style={{ fontWeight: 700, color: '#334155', marginBottom: '4px' }}>
                How ShelfIQ OCR Works:
              </div>
              1. Scans distributor invoice header for supplier name and invoice date.<br />
              2. Automatically detects tabular line items, pack sizes, quantities, and cost prices.<br />
              3. Matches extracted line items with your active store product SKUs.<br />
              4. Allows 1-click batch inventory restocking with automatic movement audit logs.
            </div>
          </TiltCard>
        </div>

        {/* Parsed Invoice Preview */}
        {parsedInvoice && (
          <div className="stagger-item" style={{ '--stagger-index': 2 }}>
            <TiltCard className="card" maxTilt={3} scale={1.005} style={{ background: 'rgba(255, 255, 255, 0.92)' }}>
              <div className="card-header">
                <div>
                  <h3>Extracted Invoice: {parsedInvoice.invoiceNumber}</h3>
                  <span style={{ fontSize: '0.8rem', color: '#64748b' }}>
                    Supplier: <strong>{parsedInvoice.supplierName}</strong> | Date: {parsedInvoice.invoiceDate}
                  </span>
                </div>
                <span className="badge badge-success">
                  <CheckCircle size={13} /> {parsedInvoice.items?.length || 0} Items Matched
                </span>
              </div>

              <div style={{ background: 'rgba(240, 253, 244, 0.9)', border: '1px solid #bbf7d0', padding: '10px 14px', borderRadius: '12px', fontSize: '0.82rem', color: '#166534', marginBottom: '16px' }}>
                {parsedInvoice.extractedTextSummary}
              </div>

              <div className="table-wrapper" style={{ marginBottom: '16px' }}>
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Detected Text</th>
                      <th>Matched SKU</th>
                      <th>Confidence</th>
                      <th style={{ textAlign: 'center' }}>Restock Qty</th>
                      <th>Unit Cost</th>
                      <th>Line Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {parsedInvoice.items?.map((item, idx) => (
                      <tr key={idx} className="stagger-item" style={{ '--stagger-index': idx }}>
                        <td>
                          <div style={{ fontSize: '0.82rem', fontWeight: 600 }}>{item.rawDescription}</div>
                        </td>
                        <td>
                          <div style={{ fontWeight: 700, color: '#4f46e5' }}>{item.matchedProductName}</div>
                          <div style={{ fontSize: '0.72rem', color: '#94a3b8' }}>SKU: {item.sku}</div>
                        </td>
                        <td>
                          <span className="badge badge-success" style={{ fontSize: '0.72rem' }}>
                            {(item.matchConfidence * 100).toFixed(0)}%
                          </span>
                        </td>
                        <td style={{ textAlign: 'center' }}>
                          <input
                            type="number"
                            min="1"
                            value={item.quantity}
                            onChange={(e) => handleQuantityChange(idx, e.target.value)}
                            className="input-glow-focus"
                            style={{ width: '65px', padding: '6px', borderRadius: '9999px', border: '1px solid #cbd5e1', textAlign: 'center', fontSize: '0.84rem' }}
                          />
                        </td>
                        <td style={{ fontSize: '0.84rem' }}>₹{item.unitCost}</td>
                        <td style={{ fontSize: '0.86rem', fontWeight: 700 }}>₹{item.lineTotal?.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '12px', borderTop: '1px solid #e2e8f0' }}>
                <div>
                  <div style={{ fontSize: '0.8rem', color: '#64748b' }}>Total Invoice Restock Value</div>
                  <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a' }}>
                    ₹<AnimatedCounter value={parsedInvoice.totalAmount || 0} decimals={2} duration={1200} />
                  </div>
                </div>

                <MagneticButton
                  onClick={handleConfirmRestock}
                  disabled={restocking}
                  className="btn btn-success"
                  style={{ padding: '11px 20px', display: 'flex', gap: '8px' }}
                >
                  <PackagePlus size={18} />
                  <span>{restocking ? 'Restocking Inventory...' : 'Confirm & Restock Stock'}</span>
                </MagneticButton>
              </div>
            </TiltCard>
          </div>
        )}
      </div>
    </div>
  );
}
