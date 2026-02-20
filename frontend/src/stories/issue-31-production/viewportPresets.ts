export const issue31ViewportOptions = {
  mobile390: {
    name: '390x844',
    styles: { width: '390px', height: '844px' },
    type: 'mobile',
  },
  tablet834: {
    name: '834x1112',
    styles: { width: '834px', height: '1112px' },
    type: 'tablet',
  },
  desktop1280: {
    name: '1280x800',
    styles: { width: '1280px', height: '800px' },
    type: 'desktop',
  },
};

export const viewportParams = (defaultViewport: keyof typeof issue31ViewportOptions) => ({
  viewport: {
    options: issue31ViewportOptions,
    defaultViewport,
  },
});
