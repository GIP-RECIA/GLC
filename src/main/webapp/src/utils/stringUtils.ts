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

const slugify = (value: string): string => {
  return String(value)
    .normalize('NFKD') // split accented characters into their base characters and diacritical marks
    .replace(/[\u0300-\u036f]/g, '') // remove all the accents, which happen to be all in the \u03xx UNICODE block.
    .trim() // trim leading or trailing whitespace
    .toLowerCase() // convert to lowercase
    .replace(/[^a-z0-9 -]/g, '') // remove non-alphanumeric characters
    .replace(/\s+/g, '-') // replace spaces with hyphens
    .replace(/-+/g, '-'); // remove consecutive hyphens
};

export { capitalize, concatenate, slugify };
