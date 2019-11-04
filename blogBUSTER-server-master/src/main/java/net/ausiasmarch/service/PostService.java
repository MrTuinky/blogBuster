package net.ausiasmarch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.ausiasmarch.bean.BeanInterface;
import net.ausiasmarch.bean.PostBean;
import net.ausiasmarch.bean.ResponseBean;
import net.ausiasmarch.connection.ConnectionInterface;
import net.ausiasmarch.dao.PostDao;
import net.ausiasmarch.factory.ConnectionFactory;
import net.ausiasmarch.factory.GsonFactory;
import net.ausiasmarch.setting.ConnectionSettings;

public class PostService implements ServiceInterface {

    HttpServletRequest oRequest = null;
    String[] frasesInicio = {"La verdad experiencial que ", "La intuición nutre lo que ", "Lo invisible es el resultado que "};
    String[] frasesMitad = {"transforma el desenfreno ", "refleja la positividad ", "abarca a la sinfonía de los hechos "};
    String[] frasesFinal = {"de la vida es la sabiduría. ", "en la irracionalidad de la excelencia. ", " la inmortalidad de la observación. "};
    public PostService(HttpServletRequest oRequest) {
        this.oRequest = oRequest;
    }

    @Override
    public String get() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConnection = oConnectionImplementation.newConnection();
        int id = Integer.parseInt(oRequest.getParameter("id"));
        PostDao oPostDao = new PostDao(oConnection);
        PostBean oPostBean = oPostDao.get(id);
        Gson oGson = GsonFactory.getGson();
        String strJson = oGson.toJson(oPostBean);
        if (oConnection != null) {
            oConnection.close();
        }
        if (oConnectionImplementation != null) {
            oConnectionImplementation.disposeConnection();
        }
        return "{\"status\":200,\"message\":" + strJson + "}";
    }

    @Override
    public String getPage() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConnection = oConnectionImplementation.newConnection();
        int iRpp = Integer.parseInt(oRequest.getParameter("rpp"));
        int iPage = Integer.parseInt(oRequest.getParameter("page"));
        List<String> orden = null;
        if (oRequest.getParameter("order") != null) {
            orden = Arrays.asList(oRequest.getParameter("order").split("\\s*,\\s*"));
        }
        PostDao oPostDao = new PostDao(oConnection);
        ArrayList alPostBean = oPostDao.getPage(iPage, iRpp, orden);
        Gson oGson = GsonFactory.getGson();
        String strJson = oGson.toJson(alPostBean);
        if (oConnection != null) {
            oConnection.close();
        }
        if (oConnectionImplementation != null) {
            oConnectionImplementation.disposeConnection();
        }
        return "{\"status\":200,\"message\":" + strJson + "}";
    }

    @Override
    public String getCount() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConnection = oConnectionImplementation.newConnection();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        PostDao oPostDao = new PostDao(oConnection);
        Integer iCount = oPostDao.getCount();
        if (oConnection != null) {
            oConnection.close();
        }
        if (oConnectionImplementation != null) {
            oConnectionImplementation.disposeConnection();
        }
        if (iCount < 0) {
            oResponseBean = new ResponseBean(500, iCount.toString());
        } else {
            oResponseBean = new ResponseBean(200, iCount.toString());
        }
        return oGson.toJson(oResponseBean);
    }

    @Override
    public String update() throws SQLException {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            Connection oConnection = oConnectionImplementation.newConnection();
            PostBean oPostBean = new PostBean();
            String data = oRequest.getParameter("data");
            oPostBean = oGson.fromJson(data, PostBean.class);
            PostDao oPostDao = new PostDao(oConnection);
            if (oPostDao.update(oPostBean) == 0) {
                oResponseBean = new ResponseBean(500, "KO");
            } else {
                oResponseBean = new ResponseBean(200, "OK");
            }
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
        }
        return oGson.toJson(oResponseBean);
    }

    @Override
    public String getAll() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConnection = oConnectionImplementation.newConnection();
        PostDao oPostDao = new PostDao(oConnection);
        Gson oGson = GsonFactory.getGson();
        String message = "";
        List<BeanInterface> listaPostBean = oPostDao.getAll();
        if (listaPostBean == null) {
            message = "\"La lista está vacia\"";
        } else {
            //oGson = gh.getGson();
            message = oGson.toJson(listaPostBean);
        }
        if (oConnection != null) {
            oConnection.close();
        }
        if (oConnectionImplementation != null) {
            oConnectionImplementation.disposeConnection();
        }
        return "{\"status\":200,\"message\":" + message + "}";
    }

    @Override
    public String insert() throws SQLException {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            Connection oConnection = oConnectionImplementation.newConnection();
            final GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation();
            PostBean oPostBean = oGson.fromJson(oRequest.getParameter("data"), PostBean.class);
            PostDao oPostDao = new PostDao(oConnection);
            if (oPostDao.insert(oPostBean) == 0) {
                oResponseBean = new ResponseBean(500, "KO");
            } else {
                oResponseBean = new ResponseBean(200, "OK");
            };
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
        }
        return oGson.toJson(oResponseBean);
    }

    @Override
    public String remove() throws SQLException {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            Connection oConnection = oConnectionImplementation.newConnection();
            PostDao oPostDao = new PostDao(oConnection);
            int id = Integer.parseInt(oRequest.getParameter("id"));
            if (oPostDao.remove(id) == 0) {
                oResponseBean = new ResponseBean(500, "KO");
            } else {
                oResponseBean = new ResponseBean(200, "OK");
            }
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
        }
        return oGson.toJson(oResponseBean);
    }

    public String fill() throws SQLException {
        HttpSession oSession = oRequest.getSession();
        ResponseBean oResponseBean;
        Gson oGson = GsonFactory.getGson();
        if (oSession.getAttribute("usuario") != null) {
            ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
            Connection oConnection = oConnectionImplementation.newConnection();
            PostDao oPostDao = new PostDao(oConnection);
            Date date1 = new GregorianCalendar(2014, Calendar.JANUARY, 1).getTime();
            Date date2 = new GregorianCalendar(2019, Calendar.DECEMBER, 31).getTime();
            int numPost = Integer.parseInt(oRequest.getParameter("number"));
            for (int i = 0; i < numPost; i++) {
                PostBean oPostBean = new PostBean();
                Date randomDate = new Date(ThreadLocalRandom.current()
                        .nextLong(date1.getTime(), date2.getTime()));
                oPostBean.setTitulo(generaTexto(1));
                oPostBean.setCuerpo(generaTexto(30));
                oPostBean.setEtiquetas(generaTexto(1));
                oPostBean.setFecha(randomDate);
                oPostDao.insert(oPostBean);
            }
            oResponseBean = new ResponseBean(200, "Insertados los registros con exito");
            if (oConnection != null) {
                oConnection.close();
            }
            if (oConnectionImplementation != null) {
                oConnectionImplementation.disposeConnection();
            }
        } else {
            oResponseBean = new ResponseBean(401, "Error: No session");
        }
        return oGson.toJson(oResponseBean);
    }

    private String generaTexto(int longitud) {
        String fraseRandom = "";
        for (int i = 0; i < longitud; i++) {
            fraseRandom += frasesInicio[(int) (Math.random() * frasesInicio.length) + 0];
            fraseRandom += frasesMitad[(int) (Math.random() * frasesMitad.length) + 0];
            fraseRandom += frasesFinal[(int) (Math.random() * frasesFinal.length) + 0];
        }
        return fraseRandom;
    }
}
