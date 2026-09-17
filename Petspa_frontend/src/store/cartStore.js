import { create } from "zustand";
import { persist } from "zustand/middleware";
import CartService from "../services/CartService";

const hasAuthToken = () => Boolean(localStorage.getItem("petspa_token"));

const calculateCart = (items) => ({
  itemDtoList: items,
  totalItem: items.reduce((sum, item) => sum + item.quantity, 0),
  totalAmount: items.reduce(
    (sum, item) => sum + Number(item.productPrice || 0) * item.quantity,
    0,
  ),
});

export const useCartStore = create(
  persist(
    (set, get) => ({
      itemDtoList: [],
      totalAmount: 0,
      totalItem: 0,
      isLoading: false,
      toast: { show: false, message: "", type: "success" },

      showToast: (message, type = "success") => {
        set({ toast: { show: true, message, type } });
        setTimeout(
          () => set({ toast: { show: false, message: "", type: "success" } }),
          3000,
        );
      },

      fetchCart: async () => {
        if (!hasAuthToken()) {
          const guestItems = get().itemDtoList.filter((item) => item.isGuest);
          set({ ...calculateCart(guestItems), isLoading: false });
          return;
        }

        set({ isLoading: true });
        try {
          const res = await CartService.getCart();
          if (res?.status === "SUCCESS" && res?.data) {
            set({
              itemDtoList: res.data.itemDtoList ?? [],
              totalAmount: res.data.totalAmount ?? 0,
              totalItem: res.data.totalItem ?? 0,
            });
          }
        } catch (error) {
          console.error("Lỗi fetch cart:", error);
        } finally {
          set({ isLoading: false });
        }
      },

      addItem: async (product, quantity = 1) => {
        if (!hasAuthToken()) {
          const items = [...get().itemDtoList.filter((item) => item.isGuest)];
          const index = items.findIndex((item) => item.productId === product.id);

          if (index >= 0) {
            items[index] = {
              ...items[index],
              quantity: items[index].quantity + quantity,
            };
          } else {
            items.push({
              id: `guest-${product.id}`,
              productId: product.id,
              productName: product.name || product.productName,
              productPrice: Number(product.price || product.productPrice || 0),
              productImage: product.imageUrl || product.productImage || "",
              quantity,
              isGuest: true,
            });
          }

          set(calculateCart(items));
          get().showToast(
            `Đã thêm ${quantity} ${product.name || product.productName} vào giỏ hàng`,
          );
          return;
        }

        try {
          await CartService.addToCart(product.id, quantity);
          await get().fetchCart();
          get().showToast(
            `Đã thêm ${quantity} ${product.name || product.productName} vào giỏ hàng`,
          );
        } catch (error) {
          console.error(error);
          get().showToast("Không thể thêm sản phẩm", "error");
        }
      },

      removeItem: async (itemId) => {
        const item = get().itemDtoList.find((entry) => entry.id === itemId);
        if (!hasAuthToken() || item?.isGuest) {
          const items = get().itemDtoList.filter(
            (entry) => entry.isGuest && entry.id !== itemId,
          );
          set(calculateCart(items));
          return;
        }

        try {
          await CartService.removeCartItem(itemId);
          await get().fetchCart();
          get().showToast("Đã xóa sản phẩm khỏi giỏ hàng", "info");
        } catch (error) {
          console.error(error);
          get().showToast("Không thể xóa sản phẩm", "error");
        }
      },

      updateQuantity: async (itemId, quantity) => {
        const item = get().itemDtoList.find((entry) => entry.id === itemId);
        if (!hasAuthToken() || item?.isGuest) {
          const items = get().itemDtoList
            .filter((entry) => entry.isGuest)
            .map((entry) =>
              entry.id === itemId ? { ...entry, quantity } : entry,
            )
            .filter((entry) => entry.quantity > 0);
          set(calculateCart(items));
          return;
        }

        try {
          await CartService.updateCartItem(itemId, quantity);
          await get().fetchCart();
        } catch (error) {
          console.error("updateQuantity error:", error);
          get().showToast("Không thể cập nhật số lượng", "error");
        }
      },

      clearCart: async () => {
        if (!hasAuthToken()) {
          set(calculateCart([]));
          return;
        }

        try {
          const res = await CartService.clearCart();
          if (res?.success) {
            set(calculateCart([]));
            get().showToast("Đã xóa giỏ hàng", "info");
          }
        } catch (error) {
          console.error(error);
          get().showToast("Không thể xóa giỏ hàng", "error");
        }
      },

      syncGuestCart: async () => {
        if (!hasAuthToken()) return;

        const guestItems = get().itemDtoList.filter((item) => item.isGuest);
        for (const item of guestItems) {
          await CartService.addToCart(item.productId, item.quantity);
        }

        if (guestItems.length > 0) set(calculateCart([]));
        await get().fetchCart();
      },
    }),
    {
      name: "petspa-cart-storage",
      partialize: (state) => ({
        itemDtoList: state.itemDtoList,
        totalAmount: state.totalAmount,
        totalItem: state.totalItem,
      }),
    },
  ),
);
