// src/components/Layout.tsx
import React from 'react';
import NavBar from './components/Navbar';

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div data-theme="cupcake">
      <NavBar />
      <main>{children}</main>
    </div>
  );
}
