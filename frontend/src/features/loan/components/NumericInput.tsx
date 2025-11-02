import { TextField, TextFieldProps } from '@mui/material';

type NumericInputProps = Omit<TextFieldProps, 'onChange' | 'value' | 'type'> & {
  value: number;
  onChange: (value: number) => void;
  min?: number;
  max?: number;
  step?: number;
  allowDecimals?: boolean;
  label?: string;
  required?: boolean;
};

export function NumericInput({
  value,
  onChange,
  min,
  max,
  step,
  allowDecimals = true,
  label,
  required,
  ...textFieldProps
}: NumericInputProps) {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const numValue = parseFloat(event.target.value) || 0;
    onChange(numValue);
  };

  return (
    <TextField
      {...textFieldProps}
      label={label}
      type="number"
      value={value || ''}
      onChange={handleChange}
      required={required}
      fullWidth
      inputProps={{
        min,
        max,
        step: step ?? (allowDecimals ? 0.01 : 1),
        ...textFieldProps.inputProps,
      }}
    />
  );
}