export interface OutbrainWidgetHandler {
  onHeightChange?: (newHeight: number) => void;
  onRecClick?: (url: string) => void;
  onOrganicClick?: (url: string) => void;
  onWidgetEvent?: (eventName:string, data: { [key: string]: any}) => void;
}

export interface OutbrainWidgetProps {
  widgetId: string;
  widgetIndex: number;
  articleUrl: string;
  partnerKey: string;
  extId?: string;
  extSecondaryId?: string;
  pubImpId?: string;
  handler?: OutbrainWidgetHandler;
}

export type NativeComponentProps = React.ComponentProps<typeof View>;