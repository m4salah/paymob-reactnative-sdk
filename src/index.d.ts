declare module 'paymob-reactnative' {
  export interface PaymobListener {
    (status: PaymentResult): void;
  }

  export interface SavedBankCard {
    maskedPan: string;
    savedCardToken: string;
    creditCard: CreditCardType;
  }

  export enum CreditCardType {
    VISA = 'VISA',
    MASTERCARD = 'MASTERCARD',
    AMERICAN_EXPRESS = 'AMERICAN_EXPRESS',
    MEEZA = 'MEEZA',
    JCB = 'JCB',
    MAESTRO = 'MAESTRO',
    OMAN_NET = 'OMAN_NET',
  }

  export enum PaymentStatus {
    SUCCESS = 'Success',
    FAIL = 'Fail',
    PENDING = 'Pending',
  }

  export type PaymentResponse = {
    status: PaymentResult; // Enum type for status
    details?: object; // Optional property of type 'object'
  };

  export interface PaymobModule {
    setAppIcon(base64Image: string): void;
    setAppName(name: string): void;
    setButtonBackgroundColor(color: string): void;
    setButtonTextColor(color: string): void;
    setSaveCardDefault(isEnabled: boolean): void;
    setShowSaveCard(isVisible: boolean): void;
    setShowConfirmationPage(isVisible: boolean): void;
    presentPayVC(
      clientSecret: string,
      publicKey: string,
      savedBankCards?: SavedBankCard[]
    ): void;
    setSdkListener(listener: PaymobListener): void;
    removeSdkListener(): void;
  }

  const Paymob: PaymobModule;

  export default Paymob;
}
