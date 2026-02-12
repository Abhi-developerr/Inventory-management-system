import { useQuery } from '@tanstack/react-query';
import { getProductByBarcode } from '../../../api/barcode.api';

export const useProductByBarcode = (barcodeNumber, options = {}) => {
  return useQuery({
    queryKey: ['products', 'barcode', barcodeNumber],
    queryFn: () => getProductByBarcode(barcodeNumber),
    enabled: !!barcodeNumber && options.enabled !== false,
    ...options,
  });
};
