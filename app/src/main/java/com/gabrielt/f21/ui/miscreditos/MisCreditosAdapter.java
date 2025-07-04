package com.gabrielt.f21.ui.miscreditos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gabrielt.f21.R;
import com.gabrielt.f21.model.FechaConverter;
import com.gabrielt.f21.model.MisCreditosView;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class MisCreditosAdapter extends RecyclerView.Adapter<MisCreditosAdapter.ViewHolderMisCreditos> {

    private List<MisCreditosView.Cuota> misCreditosLista;
    private LayoutInflater li;
    private FechaConverter fechaConverter ;

    public MisCreditosAdapter(List<MisCreditosView.Cuota> misCreditosLista, LayoutInflater li) {
        this.misCreditosLista = misCreditosLista;
        this.li = li;
        fechaConverter = new FechaConverter();
    }

    // Asignar Card
    @NonNull
    @Override
    public ViewHolderMisCreditos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.mis_creditos_card, parent, false);
        return new ViewHolderMisCreditos(view);
    }

    // Iteración, elemento actual
    @Override
    public void onBindViewHolder(@NonNull ViewHolderMisCreditos holder, int position) {
        // Si es un usuario nuevo al no tener cuotas/creditos anteriores, mostrar card personalizada
        if (misCreditosLista.isEmpty()) {
            //(En Reservas cuando esta vacia se modifican estilos para que comparta los de esta card)
            // Obtener la fecha actual en formato yyyy-MM-dd
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fechaHoy = formatoFecha.format(new Date());
            fechaHoy += "T00:00:00"; // Agregar T00:00:00 para cumplir con el formato ISO 8601
            fechaHoy = fechaConverter.convertirFechaLegibleCorta(fechaHoy); // Convertir a formato legible
            holder.tv_cantidad_creditos.setText("");
            holder.tv_creditos_usados.setText("Aún no tenés registros de créditos.");
            holder.tv_estado.setText("");
            holder.tv_fecha_pago.setText(fechaHoy);
            holder.tv_fecha_vencimiento.setText("");
            return;
        }

        // Mostrar cuotas/creditos anteriores del usuario logeado
        MisCreditosView.Cuota cuota = misCreditosLista.get(position);
        String fechaPagoLegible = fechaConverter.convertirFechaLegibleCorta(cuota.getFechaPago().toString());
        String fechaVencimientoLegible = fechaConverter.convertirFechaLegibleCorta(cuota.getFechaVencimiento().toString());

        // Mostrar las fechas en los TextViews
        holder.tv_fecha_pago.setText("Desde: " + fechaPagoLegible);
        holder.tv_fecha_vencimiento.setText("Hasta: " + fechaVencimientoLegible);

        try {
            // Obtener la fecha actual
            Date fechaActual = new Date();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd"); // Formato deseado
            String fechaHoy = formatoFecha.format(fechaActual); // Fecha actual en String
            Date fechaVencimiento = formatoFecha.parse(cuota.getFechaVencimiento().toString());
            Date fechaPago = formatoFecha.parse(cuota.getFechaPago().toString()); // Fecha de pago de la cuota

            // Comparar las fechas y asignar el estado
            if (fechaPago != null && fechaActual.after(fechaPago) && fechaActual.before(fechaVencimiento)) {
                holder.tv_estado.setText("Estado: En curso"); // Solo la cuota actual
            } else if (fechaActual.before(fechaPago)) {
                holder.tv_estado.setText("Estado: Abonada"); // Las cuotas futuras
            } else {
                holder.tv_estado.setText("Estado: Caducada"); // Las cuotas que ya pasaron
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.tv_estado.setText("Estado: Desconocido"); // En caso de error
        }

        // Mostrar la cantidad de créditos y los créditos usados
        holder.tv_cantidad_creditos.setText("Créditos: " + cuota.getPlanCantidadCreditos());
        holder.tv_creditos_usados.setText("Usados: " + cuota.getCreditosUsados());
    }

    @Override
    public int getItemCount() {
        //Minimo se va mostrar una card:
        return Math.max(1, misCreditosLista.size());
    }

    public class ViewHolderMisCreditos extends RecyclerView.ViewHolder {

        TextView tv_fecha_pago, tv_fecha_vencimiento, tv_estado, tv_cantidad_creditos, tv_creditos_usados;

        public ViewHolderMisCreditos(@NonNull View itemView) {
            super(itemView);
            tv_fecha_pago = itemView.findViewById(R.id.tv_fecha_nueva_reserva);
            tv_fecha_vencimiento = itemView.findViewById(R.id.tv_clase_desc_nueva_reserva);
            tv_estado = itemView.findViewById(R.id.tv_disponibles_nueva_reserva);
            tv_cantidad_creditos = itemView.findViewById(R.id.tv_cantidad_creditos);
            tv_creditos_usados = itemView.findViewById(R.id.tv_creditos_usados);
        }
    }
}
