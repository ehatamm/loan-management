import { useId } from 'react';
import { FormControl, InputLabel, MenuItem, Select, SelectChangeEvent } from '@mui/material';

interface FormSelectFieldProps<T extends string> {
  label: string;
  value: T;
  onChange: (value: T) => void;
  options: Array<{ value: T; label: string }>;
}

export function FormSelectField<T extends string>({
  label,
  value,
  onChange,
  options,
}: FormSelectFieldProps<T>) {
  const id = useId();
  const handleChange = (event: SelectChangeEvent<string>) => {
    onChange(event.target.value as T);
  };

  return (
    <FormControl fullWidth>
      <InputLabel id={`${id}-label`} shrink>{label}</InputLabel>
      <Select
        labelId={`${id}-label`}
        id={id}
        value={value}
        label={label}
        onChange={handleChange}
      >
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>
            {option.label}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}

