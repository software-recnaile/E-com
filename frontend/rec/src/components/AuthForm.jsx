import React from 'react';
import { Link } from 'react-router-dom';

const AuthForm = ({ title, fields, buttonText, onSubmit, onChange, formData, linkText, linkPath }) => {
  return (
    <div className="auth-container">
      <h2>{title}</h2>
      <form onSubmit={onSubmit}>
        {fields.map((field) => (
          <div key={field.name} className="form-group">
            <label>{field.placeholder}</label>
            <input
              type={field.type}
              name={field.name}
              value={formData[field.name] || ''}
              onChange={onChange}
              placeholder={field.placeholder}
              required
            />
          </div>
        ))}
        <button type="submit" className="btn-primary">
          {buttonText}
        </button>
      </form>
      <div className="auth-footer">
        <Link to={linkPath}>{linkText}</Link>
      </div>
    </div>
  );
};

export default AuthForm;