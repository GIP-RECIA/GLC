import isEmpty from 'lodash.isempty';

const capitalize = (value: string): string => {
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
};

const concatenate = (values: Array<string | undefined>, separator?: string): string => {
  let result: string = '';
  values.forEach((value, index) => {
    if (!isEmpty(value)) {
      result += value;
      if (!isEmpty(separator) && !isEmpty(values[index + 1])) result += separator;
    }
  });

  return result;
};

export { capitalize, concatenate };
