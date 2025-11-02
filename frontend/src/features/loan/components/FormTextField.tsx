import { TextField, TextFieldProps } from '@mui/material';

interface FormTextFieldProps extends Omit<TextFieldProps, 'onChange'> {
  onChange: (value: string | number) => void;
  value: string | number;
}

export function FormTextField({ onChange, ...props }: FormTextFieldProps) {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = props.type === 'number' ? Number(event.target.value) : event.target.value;
    onChange(value);
  };

  return <TextField {...props} onChange={handleChange} fullWidth required />;
}

