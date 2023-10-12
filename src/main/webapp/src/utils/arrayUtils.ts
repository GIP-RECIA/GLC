const isArrayOf = (array: Array<any>, type: 'string' | 'number' | 'bigint' | 'boolean' | 'object') => {
  if (Array.isArray(array)) {
    let hasNotRightType: boolean = false;
    array.forEach((item) => {
      if (typeof item !== type) hasNotRightType = true;
    });

    return !hasNotRightType;
  }
  return false;
};

export { isArrayOf };
