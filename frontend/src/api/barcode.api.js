import axiosInstance from './axios';

export const getProductByBarcode = async (barcodeNumber) => {
  const response = await axiosInstance.get(`/products/barcode/${barcodeNumber}`);
  return response.data.data;
};
